package com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class ObservacionItemJpaSpecification extends QueryJpaSpecification<ObservacionItemJpaQueryEntity> {

    private static final Map<String, CampoSpec<ObservacionItemJpaQueryEntity>> CAMPOS;

    static {
        var m = new LinkedHashMap<String, CampoSpec<ObservacionItemJpaQueryEntity>>();
        for (var campo : ObservacionItemCriteria.Campo.values()) {
            CampoSpec<ObservacionItemJpaQueryEntity> spec = switch (campo) {
                case REVISION_ITEM               -> CampoSpec.uuid(root -> root.get("revisionItemId"));
                case ESTADO_OBSERVACION_REVISION -> CampoSpec.texto(root -> root.get("estadoObservacionRevisionId"));
                case ASESOR_ID                   -> CampoSpec.uuid(root -> root.get("asesorId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<ObservacionItemJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
