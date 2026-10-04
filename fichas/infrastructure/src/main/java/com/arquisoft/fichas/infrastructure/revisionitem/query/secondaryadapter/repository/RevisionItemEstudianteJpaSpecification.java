package com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemEstudianteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class RevisionItemEstudianteJpaSpecification extends QueryJpaSpecification<RevisionItemEstudianteJpaQueryEntity> {

    private static final Map<String, CampoSpec<RevisionItemEstudianteJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<RevisionItemEstudianteJpaQueryEntity>> m = new LinkedHashMap<>();
        for (RevisionItemEstudianteCriteria.Campo campo : RevisionItemEstudianteCriteria.Campo.values()) {
            CampoSpec<RevisionItemEstudianteJpaQueryEntity> spec = switch (campo) {
                case ITEM            -> CampoSpec.uuid(root -> root.get("itemId"));
                case ESTADO_REVISION -> CampoSpec.texto(root -> root.get("estadoRevisionId"));
                case ESTUDIANTE_ID   -> CampoSpec.uuid(root -> root.get("estudianteId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<RevisionItemEstudianteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
