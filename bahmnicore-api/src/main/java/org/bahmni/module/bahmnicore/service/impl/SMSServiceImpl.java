package org.bahmni.module.bahmnicore.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import org.bahmni.module.bahmnicore.contract.SMS.SMSRequest;
import org.bahmni.module.bahmnicore.properties.SMSProperties;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.bahmni.module.bahmnicore.service.SMSService;
import org.openmrs.Patient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

import static org.bahmni.module.bahmnicore.util.BahmniDateUtil.convertUTCToGivenFormat;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;

@Service
public class SMSServiceImpl implements SMSService {
    private static Logger logger = LogManager.getLogger(BahmniDrugOrderService.class);

    @Autowired
    public SMSServiceImpl() {}

    @Override
    public Object sendSMS(String phoneNumber, String message) {
        try {
            SMSRequest smsRequest = new SMSRequest();
            smsRequest.setPhoneNumber(phoneNumber);
            smsRequest.setMessage(message);

            ObjectMapper Obj = new ObjectMapper();
            String jsonObject = Obj.writeValueAsString(smsRequest);
            StringEntity params = new StringEntity(jsonObject);

            HttpPost request = new HttpPost(SMSProperties.getProperty("sms.url"));
            request.addHeader("content-type", "application/json");
            request.setEntity(params);
            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpResponse response = httpClient.execute(request);

            return response.getStatusLine();
        } catch (Exception e) {
            logger.error("Exception occured in sending sms ", e);
            throw new RuntimeException("Exception occured in sending sms ", e);
        }
    }

    @Override
    public String getPrescriptionMessage(String lang, Date visitDate, Patient patient, String location, String providerDetail, String prescriptionDetail) {
        String prescriptionSMSContent = SMSProperties.getProperty("sms." + lang + ".prescriptionSMS");
        prescriptionSMSContent = prescriptionSMSContent.replace("#visitDate", convertUTCToGivenFormat(visitDate, "dd-MM-yyyy", SMSProperties.getProperty("sms.timeZone")));
        prescriptionSMSContent = prescriptionSMSContent.replace("#patientName",patient.getGivenName() + " " + patient.getFamilyName());
        prescriptionSMSContent = prescriptionSMSContent.replace("#gender", patient.getGender());
        prescriptionSMSContent = prescriptionSMSContent.replace("#age", patient.getAge().toString());
        prescriptionSMSContent = prescriptionSMSContent.replace("#doctorDetail", providerDetail);
        prescriptionSMSContent = prescriptionSMSContent.replace("#location", location);
        prescriptionSMSContent = prescriptionSMSContent.replace("#prescriptionDetails", prescriptionDetail);
        return prescriptionSMSContent;
    }

}
