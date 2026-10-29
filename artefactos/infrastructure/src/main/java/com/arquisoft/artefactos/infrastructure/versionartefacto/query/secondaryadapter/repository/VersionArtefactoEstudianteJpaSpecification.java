package com.arquisoft.artefactos.infrastructure.versionartefacto.query.secondaryadapter.repository;

import com.arquisoft.artefactos.application.versionartefacto.query.criteria.VersionArtefactoEstudianteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class VersionArtefactoEstudianteJpaSpecification extends QueryJpaSpecification<VersionArtefactoEstudianteJpaQueryEntity> {

    private static final Map<String, CampoSpec<VersionArtefactoEstudianteJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<VersionArtefactoEstudianteJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : VersionArtefactoEstudianteCriteria.Campo.values()) {
            CampoSpec<VersionArtefactoEstudianteJpaQueryEntity> spec = switch (campo) {
                case ARTEFACTO      -> CampoSpec.uuid(root -> root.get("artefactoId"));
                case VERSION        -> CampoSpec.entero(root -> root.get("version"));
                case FECHA_CREACION -> CampoSpec.fechaHora(root -> root.get("fechaCreacion"));
                case ESTUDIANTE_ID      -> CampoSpec.uuid(root -> root.get("estudianteId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<VersionArtefactoEstudianteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
