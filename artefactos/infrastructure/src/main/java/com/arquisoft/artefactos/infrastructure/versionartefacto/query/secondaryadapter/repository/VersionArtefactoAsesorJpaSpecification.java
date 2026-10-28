package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoAsesorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class VersionArtefactoAsesorJpaSpecification extends QueryJpaSpecification<VersionArtefactoAsesorJpaQueryEntity> {

    private static final Map<String, CampoSpec<VersionArtefactoAsesorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<VersionArtefactoAsesorJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : VersionArtefactoAsesorCriteria.Campo.values()) {
            CampoSpec<VersionArtefactoAsesorJpaQueryEntity> spec = switch (campo) {
                case ARTEFACTO      -> CampoSpec.uuid(root -> root.get("artefactoId"));
                case VERSION        -> CampoSpec.entero(root -> root.get("version"));
                case FECHA_CREACION -> CampoSpec.fechaHora(root -> root.get("fechaCreacion"));
                case ASESOR_ID      -> CampoSpec.uuid(root -> root.get("asesorId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<VersionArtefactoAsesorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
