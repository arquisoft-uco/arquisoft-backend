package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionJuradoCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
class EvaluacionJuradoJpaSpecification extends QueryJpaSpecification<EvaluacionJuradoJpaQueryEntity> {

    private static final Map<String, CampoSpec<EvaluacionJuradoJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<EvaluacionJuradoJpaQueryEntity>> m = new LinkedHashMap<>();
        for (EvaluacionJuradoCriteria.Campo campo : EvaluacionJuradoCriteria.Campo.values()) {
            CampoSpec<EvaluacionJuradoJpaQueryEntity> spec = switch (campo) {
                case JURADO -> CampoSpec.texto(root -> root.get("juradoNombre"));
                case JURADO_ID -> CampoSpec.uuid(root -> root.get("juradoId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<EvaluacionJuradoJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }

    Specification<EvaluacionJuradoJpaQueryEntity> deLaEvaluacion(UUID evaluacion) {
        return (root, query, cb) -> cb.equal(root.get("evaluacionId"), evaluacion);
    }
}
