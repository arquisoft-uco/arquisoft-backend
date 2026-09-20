package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemjurado.query.criteria.ObservacionItemJuradoCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
class ObservacionItemJuradoJpaSpecification extends QueryJpaSpecification<ObservacionItemJuradoJpaQueryEntity> {

    private static final Map<String, CampoSpec<ObservacionItemJuradoJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<ObservacionItemJuradoJpaQueryEntity>> m = new LinkedHashMap<>();
        for (ObservacionItemJuradoCriteria.Campo campo : ObservacionItemJuradoCriteria.Campo.values()) {
            CampoSpec<ObservacionItemJuradoJpaQueryEntity> spec = switch (campo) {
                case DESCRIPCION -> CampoSpec.texto(root -> root.get("descripcion"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<ObservacionItemJuradoJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }

    Specification<ObservacionItemJuradoJpaQueryEntity> deLaEvaluacion(UUID evaluacionCuantitativaJurado) {
        return (root, query, cb) -> cb.equal(root.get("evaluacionCuantitativaJuradoId"), evaluacionCuantitativaJurado);
    }
}
