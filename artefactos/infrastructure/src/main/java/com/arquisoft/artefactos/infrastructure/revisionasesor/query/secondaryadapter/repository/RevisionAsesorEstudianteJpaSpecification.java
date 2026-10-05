package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorEstudianteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class RevisionAsesorEstudianteJpaSpecification extends QueryJpaSpecification<RevisionAsesorEstudianteJpaQueryEntity> {

    private static final Map<String, CampoSpec<RevisionAsesorEstudianteJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<RevisionAsesorEstudianteJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : RevisionAsesorEstudianteCriteria.Campo.values()) {
            CampoSpec<RevisionAsesorEstudianteJpaQueryEntity> spec = switch (campo) {
                case VERSION_ARTEFACTO      -> CampoSpec.uuid(root -> root.get("versionArtefactoId"));
                case ESTADO_REVISION_ASESOR -> CampoSpec.texto(root -> root.get("estadoRevisionAsesorId"));
                case ESTUDIANTE_ID          -> CampoSpec.uuid(root -> root.get("estudianteId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<RevisionAsesorEstudianteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
