package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class EvaluacionJpaSpecification extends QueryJpaSpecification<EvaluacionJpaQueryEntity> {

    private static final Map<String, CampoSpec<EvaluacionJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<EvaluacionJpaQueryEntity>> m = new LinkedHashMap<>();
        for (EvaluacionCriteria.Campo campo : EvaluacionCriteria.Campo.values()) {
            CampoSpec<EvaluacionJpaQueryEntity> spec = switch (campo) {
                case ESTADO -> CampoSpec.texto(root -> root.get("estadoId"));
                case PROYECTO -> CampoSpec.texto(root -> root.get("entregableProyecto"));
                case ENTREGABLE_ID -> CampoSpec.uuid(root -> root.get("entregableId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<EvaluacionJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
