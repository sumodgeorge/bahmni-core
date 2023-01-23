package org.openmrs.module.bahmniemrapi.encountertransaction.command.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openmrs.Concept;
import org.openmrs.ConceptClass;
import org.openmrs.ConceptDatatype;
import org.openmrs.ConceptMap;
import org.openmrs.ConceptMapType;
import org.openmrs.ConceptName;
import org.openmrs.ConceptReferenceTerm;
import org.openmrs.ConceptSource;
import org.openmrs.api.APIException;
import org.openmrs.api.AdministrationService;
import org.openmrs.api.ConceptService;
import org.openmrs.api.context.Context;
import org.openmrs.module.bahmniemrapi.diagnosis.contract.BahmniDiagnosisRequest;
import org.openmrs.module.bahmniemrapi.encountertransaction.command.EncounterDataPreSaveCommand;
import org.openmrs.module.bahmniemrapi.encountertransaction.contract.BahmniEncounterTransaction;
import org.openmrs.module.emrapi.EmrApiProperties;
import org.openmrs.module.emrapi.encounter.domain.EncounterTransaction;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class BahmniDiagnosisAnswerConceptSaveCommandImpl implements EncounterDataPreSaveCommand {

    private AdministrationService adminService;

    private ConceptService conceptService;

    private EmrApiProperties emrApiProperties;;

    public static final String CONCEPT_CLASS_DIAGNOSIS = "Diagnosis";

    public static final String CONCEPT_DATATYPE_NA = "N/A";

    public static final String DEFAULT_CONCEPT_SET_FOR_DIAGNOSIS_CONCEPT = "Unclassified";

    public static final String GP_DEFAULT_CONCEPT_SET_FOR_DIAGNOSIS_CONCEPT_UUID = "ts.diagnosisSetForNewConcepts";

    private static final String TERMINOLOGY_SERVER_CODED_ANSWER_DELIMITER = "/";     // eg: SCT/12345, ICD 10 - WHO/W54.0XXA

    @Autowired
    public BahmniDiagnosisAnswerConceptSaveCommandImpl(@Qualifier("adminService") AdministrationService administrationService, ConceptService conceptService, EmrApiProperties emrApiProperties) {
        this.adminService = administrationService;
        this.conceptService = conceptService;
        this.emrApiProperties = emrApiProperties;
    }

    @Override
    public BahmniEncounterTransaction update(BahmniEncounterTransaction bahmniEncounterTransaction) {
        List<BahmniDiagnosisRequest> bahmniDiagnoses = bahmniEncounterTransaction.getBahmniDiagnoses();
        bahmniDiagnoses.stream().forEach(bahmniDiagnosis -> {
            EncounterTransaction.Concept codedAnswer = bahmniDiagnosis.getCodedAnswer();
            if(codedAnswer != null) {
                String codedAnswerUuid = codedAnswer.getUuid();
                boolean createNewConcept = codedAnswerUuid.contains(TERMINOLOGY_SERVER_CODED_ANSWER_DELIMITER);
                if(createNewConcept) {
                    String diagnosisConceptSourceCode = codedAnswerUuid.split(TERMINOLOGY_SERVER_CODED_ANSWER_DELIMITER)[0];
                    String diagnosisConceptReferenceTermCode = codedAnswerUuid.split(TERMINOLOGY_SERVER_CODED_ANSWER_DELIMITER)[1];
                    ConceptSource conceptSource = getConceptSource(diagnosisConceptSourceCode);
                    if(conceptSource == null)
                        throw new APIException("Concept Source " + diagnosisConceptSourceCode + " not found");
                    Concept existingDiagnosisAnswerConcept = conceptService.getConceptByMapping(diagnosisConceptReferenceTermCode, conceptSource.getName());
                    if(existingDiagnosisAnswerConcept == null) {
                        Concept newDiagnosisAnswerConcept = createNewDiagnosisConcept(codedAnswer.getName(), codedAnswer.getName(), diagnosisConceptReferenceTermCode, conceptSource);
                        codedAnswer.setUuid(newDiagnosisAnswerConcept.getUuid());
                        addNewDiagnosisConceptToDiagnosisSet(newDiagnosisAnswerConcept);
                    } else {
                        codedAnswer.setUuid(existingDiagnosisAnswerConcept.getUuid());
                    }
                }
            }
        });
        return bahmniEncounterTransaction;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private ConceptSource getConceptSource(String conceptSourceCode) {
        if(StringUtils.isBlank(conceptSourceCode))
            return null;
        List<ConceptSource> allConceptSources = conceptService.getAllConceptSources(false);
        Optional<ConceptSource> conceptSource = allConceptSources.stream().filter(cs ->
                                conceptSourceCode.equalsIgnoreCase(cs.getName()) ||
                                conceptSourceCode.equalsIgnoreCase(cs.getHl7Code()) ||
                                conceptSourceCode.equalsIgnoreCase(cs.getUniqueId())).findFirst();
        return conceptSource.isPresent() ? conceptSource.get() : null;
    }

    private Concept createNewDiagnosisConcept(String name, String preferredName, String conceptReferenceTermCode, ConceptSource conceptSource) {
        Concept concept = getConcept(name, preferredName);
        ConceptMap conceptMap = getConceptMap(name, conceptReferenceTermCode, conceptSource);
        updateConceptMap(concept, conceptMap);
        conceptService.saveConcept(concept);
        return concept;
    }

    private Concept getConcept(String name, String preferredName) {
        Concept concept = new Concept();

        ConceptClass diagnosisConceptClass = conceptService.getConceptClassByName(CONCEPT_CLASS_DIAGNOSIS);
        concept.setConceptClass(diagnosisConceptClass);

        ConceptDatatype diagnosisConceptDataType = conceptService.getConceptDatatypeByName(CONCEPT_DATATYPE_NA);
        concept.setDatatype(diagnosisConceptDataType);

        ConceptName fullySpecifiedName = new ConceptName(name, Context.getLocale());
        concept.setFullySpecifiedName(fullySpecifiedName);

        if(!fullySpecifiedName.equals(preferredName)) {
            ConceptName shortName = new ConceptName(preferredName, Context.getLocale());
            concept.setShortName(shortName);
        }
        return concept;
    }

    private ConceptMap getConceptMap(String name, String conceptReferenceTermCode, ConceptSource conceptSource) {
        ConceptReferenceTerm conceptReferenceTerm = new ConceptReferenceTerm(conceptSource, conceptReferenceTermCode, name);
        ConceptMapType sameAsConceptMapType = conceptService.getConceptMapTypeByUuid(ConceptMapType.SAME_AS_MAP_TYPE_UUID);
        ConceptMap conceptMap = new ConceptMap(conceptReferenceTerm, sameAsConceptMapType);
        return conceptMap;
    }

    private void updateConceptMap(Concept concept, ConceptMap conceptMap) {
        Collection<ConceptMap> conceptMappings = concept.getConceptMappings();
        conceptMappings.add(conceptMap);
        concept.setConceptMappings(conceptMappings);
    }

    private void addNewDiagnosisConceptToDiagnosisSet(Concept diagnosisConcept) {
        String diagnosisSetForNewConcepts = adminService.getGlobalProperty(GP_DEFAULT_CONCEPT_SET_FOR_DIAGNOSIS_CONCEPT_UUID);
        Concept diagnosisConceptSet = null;
        if(StringUtils.isNotBlank(diagnosisSetForNewConcepts)) {
            diagnosisConceptSet = conceptService.getConceptByUuid(diagnosisSetForNewConcepts);
        } else {
            Collection<Concept> diagnosisSets = emrApiProperties.getDiagnosisSets();
            Optional<Concept> optionalConcept = diagnosisSets.stream().filter(c -> c.getName().getName().equals(DEFAULT_CONCEPT_SET_FOR_DIAGNOSIS_CONCEPT) && !c.getRetired()).findFirst();
            if(optionalConcept.isPresent()) {
                diagnosisConceptSet = optionalConcept.get();
            }
        }
        if(diagnosisConceptSet == null)
            throw new APIException("Concept Set " + DEFAULT_CONCEPT_SET_FOR_DIAGNOSIS_CONCEPT + " not found");

        diagnosisConceptSet.addSetMember(diagnosisConcept);
        conceptService.saveConcept(diagnosisConceptSet);
    }
}
