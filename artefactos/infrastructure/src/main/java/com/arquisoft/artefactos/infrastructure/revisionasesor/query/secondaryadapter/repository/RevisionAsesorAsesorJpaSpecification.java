package com.arquisoft.artefactos.infrastructure.revisionasesor.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.revisionasesor.query.criteria.RevisionAsesorAsesorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class RevisionAsesorAsesorJpaSpecification extends QueryJpaSpecification<RevisionAsesorAsesorJpaQueryEntity> {

    private static final Map<String, CampoSpec<RevisionAsesorAsesorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<RevisionAsesorAsesorJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : RevisionAsesorAsesorCriteria.Campo.values()) {
            CampoSpec<RevisionAsesorAsesorJpaQueryEntity> spec = switch (campo) {
                case VERSION_ARTEFACTO      -> CampoSpec.uuid(root -> root.get("versionArtefactoId"));
                case ESTADO_REVISION_ASESOR -> CampoSpec.texto(root -> root.get("estadoRevisionAsesorId"));
                case ASESOR_ID          -> CampoSpec.uuid(root -> root.get("asesorId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<RevisionAsesorAsesorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
