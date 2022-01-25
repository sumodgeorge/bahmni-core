UPDATE global_property SET property_value = "select distinct
          concat(pn.given_name,' ', pn.family_name) as name,
          pi.identifier as identifier,
          concat('',p.uuid) as uuid,
          IF(va.value_reference = \"Admitted\", \"true\", \"false\") as hasBeenAdmitted
        from visit v
        join person_name pn on v.patient_id = pn.person_id and pn.voided = 0 and pn.preferred = 1
        join patient_identifier pi on v.patient_id = pi.patient_id and pi.voided=0 and pi.preferred = 1
        join patient_identifier_type pit on pi.identifier_type = pit.patient_identifier_type_id
        join global_property gp on gp.property=\"bahmni.primaryIdentifierType\" and gp.property_value=pit.uuid
        join person p on p.person_id = v.patient_id and p.voided = 0
        join location l on l.uuid = ${visit_location_uuid} and v.location_id = l.location_id
        left outer join visit_attribute va on va.visit_id = v.visit_id and va.attribute_type_id = (
          select visit_attribute_type_id from visit_attribute_type where name=\"Admission Status\"
        ) and va.voided = 0
        where v.date_stopped is null AND v.voided = 0;"
where property = "emrapi.sqlSearch.activePatients";

