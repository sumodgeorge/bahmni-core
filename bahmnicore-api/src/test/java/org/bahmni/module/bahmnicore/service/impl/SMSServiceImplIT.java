package org.bahmni.module.bahmnicore.service.impl;

import org.bahmni.module.bahmnicore.properties.SMSProperties;
import org.bahmni.test.builder.PersonBuilder;
import org.bahmni.test.builder.VisitBuilder;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.openmrs.Visit;
import org.openmrs.Person;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest({SMSProperties.class})
@PowerMockIgnore("javax.management.*")
public class SMSServiceImplIT {

    private SMSServiceImpl smsService;

    @Rule
    public ExpectedException expectedEx = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        smsService = new SMSServiceImpl();
    }

    @Test
    public void shouldReturnPrescriptionSMS() throws ParseException {
        PowerMockito.mockStatic(SMSProperties.class);
        when(SMSProperties.getProperty("sms.en.prescriptionSMS")).thenReturn("Date: #visitDate\nPrescription For Patient: #patientName, #gender, #age years. \nDoctor: #doctorDetail (#location)\n#prescriptionDetails");
        when(SMSProperties.getProperty("sms.timeZone")).thenReturn("IST");
        Date visitDate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("January 30, 2023");
        Date birthDate = new SimpleDateFormat("MMMM d, yyyy", Locale.ENGLISH).parse("January 30, 2010");
        Person person = new PersonBuilder().withUUID("puuid").withPersonName("testPersonName").build();
        person.setGender("M");
        person.setBirthdate(birthDate);
        Visit visit = new VisitBuilder().withPerson(person).withUUID("vuuid").withStartDatetime(visitDate).build();

        String prescriptionContent = smsService.getPrescriptionMessage("en", visit.getStartDatetime(), visit.getPatient(), "Bahmni", "Superman", "1. Paracetamol 150 mg/ml, 50 ml, Immediately-1 Days, start from 31-01-2023");
        String expectedPrescriptionContent = "Date: 30-01-2023\n" +
                "Prescription For Patient: testPersonName null, M, 13 years. \n" +
                "Doctor: Superman (Bahmni)\n" +
                "1. Paracetamol 150 mg/ml, 50 ml, Immediately-1 Days, start from 31-01-2023";
        assertEquals(expectedPrescriptionContent, prescriptionContent);
    }

    @Test
    public void shouldThrowNullPointerExceptionOnNullUrl() throws Exception {
        PowerMockito.mockStatic(SMSProperties.class);
        when(SMSProperties.getProperty("sms.url")).thenReturn(null);
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Exception occured in sending sms");
        expectedEx.expectCause(instanceOf(java.lang.NullPointerException.class));
        smsService.sendSMS("+919999999999", "Welcome");
    }

    @Test
    public void shouldNotThrowNullPointerExceptionOnValidUrl() throws Exception {
        PowerMockito.mockStatic(SMSProperties.class);
        when(SMSProperties.getProperty("sms.url")).thenReturn("http://google.com");
        expectedEx.expect(RuntimeException.class);
        expectedEx.expectMessage("Exception occured in sending sms");
        expectedEx.isAnyExceptionExpected();
        expectedEx.expectCause(not(instanceOf(java.lang.NullPointerException.class)));
        smsService.sendSMS("+919999999999", "Welcome");
    }

}
