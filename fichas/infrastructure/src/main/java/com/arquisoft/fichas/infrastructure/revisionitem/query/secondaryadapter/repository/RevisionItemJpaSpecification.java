package com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.revisionitem.query.criteria.RevisionItemCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class RevisionItemJpaSpecification extends QueryJpaSpecification<RevisionItemJpaQueryEntity> {

    private static final Map<String, CampoSpec<RevisionItemJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<RevisionItemJpaQueryEntity>> m = new LinkedHashMap<>();
        for (RevisionItemCriteria.Campo campo : RevisionItemCriteria.Campo.values()) {
            CampoSpec<RevisionItemJpaQueryEntity> spec = switch (campo) {
                case ITEM            -> CampoSpec.uuid(root -> root.get("itemId"));
                case ESTADO_REVISION -> CampoSpec.texto(root -> root.get("estadoRevisionId"));
                case ASESOR_ID       -> CampoSpec.uuid(root -> root.get("asesorId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<RevisionItemJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
