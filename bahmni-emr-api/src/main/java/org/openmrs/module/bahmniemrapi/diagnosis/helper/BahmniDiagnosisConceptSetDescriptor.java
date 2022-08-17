package org.openmrs.module.bahmniemrapi.diagnosis.helper;

import org.openmrs.*;
import org.openmrs.module.emrapi.descriptor.ConceptSetDescriptor;
import org.openmrs.module.emrapi.descriptor.MissingConceptException;
import org.openmrs.module.emrapi.diagnosis.Diagnosis;
import org.openmrs.module.emrapi.diagnosis.Diagnosis.Certainty;
import org.openmrs.module.emrapi.diagnosis.Diagnosis.Order;



import org.openmrs.api.ConceptService;
import org.openmrs.module.emrapi.EmrApiConstants;
import org.openmrs.module.emrapi.descriptor.ConceptSetDescriptorField;
import org.openmrs.module.emrapi.diagnosis.CodedOrFreeTextAnswer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Metadata describing how a diagnosis is represented as an Obs group.
 */
public class BahmniDiagnosisConceptSetDescriptor extends ConceptSetDescriptor {

    public static final String SAME_AS = "SAME-AS";
    public static final String BAHMNICORE_VERSION = "BAHMNI-CORE-1.0";

    private Concept diagnosisSetConcept;
    private Concept codedDiagnosisConcept;
    private Concept nonCodedDiagnosisConcept;
    private Concept diagnosisOrderConcept;
    private Concept diagnosisCertaintyConcept;

    private ConceptSource emrConceptSource;

    public BahmniDiagnosisConceptSetDescriptor(ConceptService conceptService, ConceptSource emrConceptSource) {
        setup(conceptService, EmrApiConstants.EMR_CONCEPT_SOURCE_NAME, ConceptSetDescriptorField.required("diagnosisSetConcept", EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_CONCEPT_SET),
                ConceptSetDescriptorField.required("codedDiagnosisConcept", EmrApiConstants.CONCEPT_CODE_CODED_DIAGNOSIS),
                ConceptSetDescriptorField.required("nonCodedDiagnosisConcept", EmrApiConstants.CONCEPT_CODE_NON_CODED_DIAGNOSIS),
                ConceptSetDescriptorField.required("diagnosisOrderConcept", EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_ORDER),
                ConceptSetDescriptorField.required("diagnosisCertaintyConcept", EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_CERTAINTY));
        this.emrConceptSource = emrConceptSource;
    }

    /**
     * Used for testing -- in production you'll use the constructor that takes ConceptService
     */
    public BahmniDiagnosisConceptSetDescriptor() {
    }

    @Override
    protected Concept getMappingConcept(ConceptService conceptService, String primaryConceptCode, String conceptSourceName) {
        List<Concept> primaryConcepts = conceptService.getConceptsByMapping(primaryConceptCode, conceptSourceName, false);
        if (primaryConcepts.size() < 1)
            throw new MissingConceptException("Couldn't find primary concept for " + this.getClass().getSimpleName() + " which should be mapped as " + conceptSourceName + ":" + primaryConceptCode);
        return primaryConcepts.size() > 1 ? getBahmniDiagnosisConcept(primaryConcepts, conceptSourceName) :
                primaryConcepts.get(0);
    }

    private Concept getBahmniDiagnosisConcept(List<Concept> primaryConcepts, String conceptSourceName) {
        List<Concept> filteredConcepts = new ArrayList<>();
            primaryConcepts.stream().forEach(primaryConcept -> {
                Collection<ConceptMap> conceptMaps = primaryConcept.getConceptMappings();
                conceptMaps.stream().forEach(conceptMap -> { if(conceptMap.getConceptReferenceTerm().getConceptSource().getName().equals(conceptSourceName)
                        && conceptMap.getConceptMapType().getName().equals(SAME_AS))
                        filteredConcepts.add(primaryConcept);
                });
            });

        if (filteredConcepts.size() > 1) {
            Concept primaryConcept = filteredConcepts.stream().
                    filter(concept -> concept.getVersion().equals(BAHMNICORE_VERSION)).findFirst().orElse(null);

            if (primaryConcept == null)
                throw new IllegalArgumentException("found more than one concept with" + conceptSourceName + "for concepts"
                        + filteredConcepts.get(0) + "and" + filteredConcepts.get(1));
            else return primaryConcept;
        }

        if (filteredConcepts.size() < 1)
            throw new RuntimeException("found no matching concept with" + conceptSourceName);

        return filteredConcepts.get(0);

    }


    public Concept getDiagnosisSetConcept() {
        return diagnosisSetConcept;
    }

    public Concept getCodedDiagnosisConcept() {
        return codedDiagnosisConcept;
    }

    public Concept getNonCodedDiagnosisConcept() {
        return nonCodedDiagnosisConcept;
    }

    public Concept getDiagnosisOrderConcept() {
        return diagnosisOrderConcept;
    }

    public Concept getDiagnosisCertaintyConcept() {
        return diagnosisCertaintyConcept;
    }

    public void setDiagnosisSetConcept(Concept diagnosisSetConcept) {
        this.diagnosisSetConcept = diagnosisSetConcept;
    }

    public void setCodedDiagnosisConcept(Concept codedDiagnosisConcept) {
        this.codedDiagnosisConcept = codedDiagnosisConcept;
    }

    public void setNonCodedDiagnosisConcept(Concept nonCodedDiagnosisConcept) {
        this.nonCodedDiagnosisConcept = nonCodedDiagnosisConcept;
    }

    public void setDiagnosisOrderConcept(Concept diagnosisOrderConcept) {
        this.diagnosisOrderConcept = diagnosisOrderConcept;
    }

    public void setDiagnosisCertaintyConcept(Concept diagnosisCertaintyConcept) {
        this.diagnosisCertaintyConcept = diagnosisCertaintyConcept;
    }

    public void setEmrConceptSource(ConceptSource emrConceptSource) {
        this.emrConceptSource = emrConceptSource;
    }

    public boolean isDiagnosis(Obs obsGroup) {
        return obsGroup.getConcept().equals(diagnosisSetConcept);
    }

    public boolean isPrimaryDiagnosis(Obs obsGroup) {
        return isDiagnosis(obsGroup) && hasDiagnosisOrder(obsGroup, EmrApiConstants.CONCEPT_CODE_DIAGNOSIS_ORDER_PRIMARY);
    }

    private boolean hasDiagnosisOrder(Obs obsGroup, String codeForDiagnosisOrderToCheckFor) {
        Obs orderObs = findMember(obsGroup, diagnosisOrderConcept);
        //return orderObs.getValueCoded()
        throw new RuntimeException("Not Yet Implemented");
    }

    public Diagnosis toDiagnosis(Obs obsGroup) {
        if (!isDiagnosis(obsGroup)) {
            throw new IllegalArgumentException("Not an obs group for a diagnosis: " + obsGroup);
        }

        Obs orderObs = findMember(obsGroup, diagnosisOrderConcept);
        Obs certaintyObs = findMember(obsGroup, diagnosisCertaintyConcept);
        Obs codedObs = findMember(obsGroup, codedDiagnosisConcept);
        Obs nonCodedObs = null;
        if (codedObs == null) {
            nonCodedObs = findMember(obsGroup, nonCodedDiagnosisConcept);
        }
        if (codedObs == null && nonCodedObs == null) {
            throw new IllegalArgumentException("Obs group doesn't contain a coded or non-coded diagnosis: " + obsGroup);
        }
        CodedOrFreeTextAnswer diagnosisValue = buildFrom(codedObs, nonCodedObs);
        Diagnosis diagnosis = new Diagnosis(diagnosisValue, getDiagnosisOrderFrom(orderObs));
        if (certaintyObs != null) {
            diagnosis.setCertainty(getDiagnosisCertaintyFrom(certaintyObs));
        }
        diagnosis.setExistingObs(obsGroup);
        return diagnosis;
    }

    private Order getDiagnosisOrderFrom(Obs obs) {
        String mapping = findMapping(obs.getValueCoded());
        return Order.parseConceptReferenceCode(mapping);

    }

    private Certainty getDiagnosisCertaintyFrom(Obs certaintyObs) {
        String mapping = findMapping(certaintyObs.getValueCoded());
        return Certainty.parseConceptReferenceCode(mapping);
    }

    private String findMapping(Concept concept) {
        for (ConceptMap conceptMap : concept.getConceptMappings()) {
            ConceptReferenceTerm conceptReferenceTerm = conceptMap.getConceptReferenceTerm();
            if (conceptReferenceTerm.getConceptSource().equals(emrConceptSource)) {
                return conceptReferenceTerm.getCode();
            }
        }
        return null;
    }

    private CodedOrFreeTextAnswer buildFrom(Obs codedObs, Obs nonCodedObs) {
        if (codedObs != null) {
            if (codedObs.getValueCodedName() != null) {
                return new CodedOrFreeTextAnswer(codedObs.getValueCodedName());
            } else {
                return new CodedOrFreeTextAnswer(codedObs.getValueCoded());
            }
        } else {
            return new CodedOrFreeTextAnswer(nonCodedObs.getValueText());
        }
    }

}
