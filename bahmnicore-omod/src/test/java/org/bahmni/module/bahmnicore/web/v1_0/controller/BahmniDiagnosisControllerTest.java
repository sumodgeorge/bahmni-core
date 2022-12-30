package org.bahmni.module.bahmnicore.web.v1_0.controller;

import org.bahmni.module.bahmnicore.service.BahmniDiagnosisService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.openmrs.Concept;
import org.openmrs.ConceptName;
import org.openmrs.ConceptSearchResult;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.context.Context;
import org.openmrs.module.emrapi.concept.EmrConceptService;
import org.openmrs.module.webservices.rest.SimpleObject;
import org.openmrs.util.LocaleUtility;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

@PowerMockIgnore("javax.management.*")
@RunWith(PowerMockRunner.class)
@PrepareForTest(Context.class)
public class BahmniDiagnosisControllerTest {

    @Mock
    private BahmniDiagnosisService bahmniDiagnosisService;

    @Mock
    private EmrConceptService emrService;

    @Mock
    private AdministrationService administrationService;

    @InjectMocks
    private BahmniDiagnosisController bahmniDiagnosisController;

    String searchTerm = "Malaria";
    int searchLimit = 20;
    String locale = LocaleUtility.getDefaultLocale().toString();
    List<Locale> locales =  Collections.singletonList(LocaleUtility.getDefaultLocale());

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        PowerMockito.mockStatic(Context.class);
        when(Context.getAdministrationService()).thenReturn(administrationService);
        when(administrationService.getAllowedLocales()).thenReturn(locales);
    }

    @Test
    public void shouldSearchDiagnosisByNameFromDiagnosisSetOfSetsWhenNoExternalTerminologyServerUsed() throws Exception {
        Concept malariaConcept = new Concept();
        ConceptName malariaConceptName = new ConceptName(searchTerm, LocaleUtility.getDefaultLocale());
        String malariaConceptUuid = "uuid1";
        malariaConcept.setUuid(malariaConceptUuid);
        malariaConcept.setFullySpecifiedName(malariaConceptName);
        malariaConcept.setPreferredName(malariaConceptName);
        ConceptSearchResult conceptSearchResult = new ConceptSearchResult(searchTerm, malariaConcept, malariaConceptName);

        when(emrService.conceptSearch(searchTerm, LocaleUtility.getDefaultLocale(), null, Collections.EMPTY_LIST, Collections.EMPTY_LIST, searchLimit)).thenReturn(Collections.singletonList(conceptSearchResult));

        List<SimpleObject> searchResults = (List< SimpleObject >)bahmniDiagnosisController.search(searchTerm, searchLimit, locale);

        assertNotNull(searchResults);
        assertEquals(searchResults.size(), 1);
        assertEquals(searchResults.get(0).get("conceptName"), searchTerm);
        assertEquals(searchResults.get(0).get("conceptUuid"), malariaConceptUuid);
    }

    @Test
    public void shouldSearchDiagnosisByNameFromExternalTerminologyServer() throws Exception {
        when(bahmniDiagnosisService.isExternalTerminologyServerLookupNeeded()).thenReturn(true);
        List<SimpleObject> searchResults = (List< SimpleObject >)bahmniDiagnosisController.search(searchTerm, searchLimit, locale);
        assertNotNull(searchResults);
        //To Do : SNOMED Module API
    }
}
