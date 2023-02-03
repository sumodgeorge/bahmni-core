package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.contract.SMS.PrescriptionSMS;
import org.bahmni.module.bahmnicore.properties.SMSProperties;
import org.bahmni.module.bahmnicore.service.BahmniDrugOrderService;
import org.bahmni.module.bahmnicore.service.BahmniObsService;
import org.bahmni.module.bahmnicore.service.BahmniVisitService;
import org.bahmni.module.bahmnicore.service.SMSService;
import org.bahmni.test.builder.PersonBuilder;
import org.bahmni.test.builder.VisitBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.openmrs.Visit;
import org.openmrs.Person;
import org.openmrs.Location;
import org.openmrs.PersonAttribute;
import org.openmrs.PersonAttributeType;
import org.openmrs.api.ConceptService;
import org.openmrs.module.bahmniemrapi.drugorder.contract.BahmniDrugOrder;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.HashSet;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SMSProperties.class})
public class BahmniDrugOrderControllerTest {

    @Mock
    ConceptService conceptService;
    @Mock
    BahmniDrugOrderService bahmniDrugOrderService;
    @Mock
    BahmniVisitService bahmniVisitService;
    @Mock
    SMSService smsService;
    @Mock
    BahmniObsService bahmniObsService;

    @InjectMocks
    BahmniDrugOrderController bahmniDrugOrderController;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
    }

    @Test
    public void shouldReturnNullIfConceptNotFound() throws Exception {
        String drugConceptSetName = "All TB Drugs";
        when(conceptService.getConceptByName(drugConceptSetName)).thenReturn(null);
        Set<org.openmrs.Concept> drugConcepts = bahmniDrugOrderController.getDrugConcepts(drugConceptSetName);
        assertNull(drugConcepts);
    }

    @Test
    public void shouldReturnNullIfDrugConceptNameIsNull() {
        Set<org.openmrs.Concept> drugConcepts = bahmniDrugOrderController.getDrugConcepts(null);
        assertNull(drugConcepts);
    }

    @Test
    public void shouldReturnErrorResponseForSendingPrescriptionSMS() throws Exception {
        PrescriptionSMS prescriptionSMS = new PrescriptionSMS();
        prescriptionSMS.setVisitUuid("visit-uuid");
        prescriptionSMS.setLocale("en");
        Visit visit = createVisitForTest();
        String sampleSMSContent = "Date: 30-01-2023\n" +
                "Prescription For Patient: testPersonName null, M, 13 years.\n" +
                "Doctor: Superman (Bahmni)\n" +
                "1. Paracetamol 150 mg/ml, 50 ml, Immediately-1 Days, start from 31-01-2023";

        PowerMockito.mockStatic(SMSProperties.class);
        when(SMSProperties.getProperty("sms.en.prescriptionSMS")).thenReturn("Date: #visitDate\nPrescription For Patient: #patientName, #gender, #age years.\nDoctor: #doctorDetail (#location)\n#prescriptionDetails");
        when(SMSProperties.getProperty("sms.timeZone")).thenReturn("IST");
        when(SMSProperties.getProperty("sms.url")).thenReturn("dummyurl");
        when(bahmniVisitService.getVisitSummary(prescriptionSMS.getVisitUuid())).thenReturn(visit);
        when(bahmniVisitService.getParentLocationNameForVisit(visit.getLocation())).thenReturn(visit.getLocation().getName());
        when(bahmniDrugOrderService.getMergedDrugOrderMap(new ArrayList<BahmniDrugOrder>())).thenReturn(null);
        when(bahmniDrugOrderService.getAllProviderAsString(new ArrayList<BahmniDrugOrder>())).thenReturn("Superman");
        when(bahmniDrugOrderService.getPrescriptionAsString(null)).thenReturn("1. Paracetamol 150 mg/ml, 50 ml, Immediately-1 Days, start from 31-01-2023");
        when(smsService.getPrescriptionMessage(prescriptionSMS.getLocale(), visit.getStartDatetime(), visit.getPatient(),
                "Bahmni", "Superman", "1. Paracetamol 150 mg/ml, 50 ml, Immediately-1 Days, start from 31-01-2023"))
                .thenReturn(sampleSMSContent);
        bahmniDrugOrderController.sendPrescriptionSMS(prescriptionSMS);

        verify(smsService, times(1)).sendSMS("+919999999999", sampleSMSContent);
    }

    private Visit createVisitForTest() throws Exception {
        Date visitDate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("January 30, 2023");
        Date birthDate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("January 30, 2010");
        Person person = new PersonBuilder().withUUID("puuid").withPersonName("testPersonName").build();
        person.setGender("M");
        person.setBirthdate(birthDate);
        PersonAttribute pa = new PersonAttribute();
        pa.setValue("+919999999999");
        PersonAttributeType pat = new PersonAttributeType();
        pat.setName("phoneNumber");
        pa.setAttributeType(pat);
        Set<PersonAttribute> paSet = new HashSet<>();
        paSet.add(pa);
        person.setAttributes(paSet);

        Visit visit = new VisitBuilder().withPerson(person).withUUID("visit-uuid").withStartDatetime(visitDate).build();
        Location location = new Location();
        location.setName("Bahmni");
        visit.setLocation(location);

        return visit;
    }
}