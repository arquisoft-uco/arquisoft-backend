package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.query.criteria.EvaluacionesAsignadasJuradoCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
class EvaluacionAsignadaJuradoJpaSpecification
        extends QueryJpaSpecification<EvaluacionAsignadaJuradoJpaQueryEntity> {

    private static final Map<String, CampoSpec<EvaluacionAsignadaJuradoJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<EvaluacionAsignadaJuradoJpaQueryEntity>> m = new LinkedHashMap<>();
        m.put(EvaluacionesAsignadasJuradoCriteria.Campo.ESTADO_JURADO.getClave(),
                CampoSpec.texto(root -> root.get("estadoJuradoId")));
        m.put(EvaluacionesAsignadasJuradoCriteria.Campo.ESTADO.getClave(),
                CampoSpec.texto(root -> root.get("estadoId")));
        m.put(EvaluacionesAsignadasJuradoCriteria.Campo.PROYECTO.getClave(),
                CampoSpec.texto(root -> root.get("entregableProyecto")));
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<EvaluacionAsignadaJuradoJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }

    Specification<EvaluacionAsignadaJuradoJpaQueryEntity> delJurado(UUID jurado) {
        return (root, query, cb) -> cb.equal(root.get("juradoId"), jurado);
    }
}
