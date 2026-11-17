package com.arquisoft.evaluaciones.infrastructure.observacionitemasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemasesor.query.criteria.ObservacionItemAsesorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
class ObservacionItemAsesorJpaSpecification extends QueryJpaSpecification<ObservacionItemAsesorJpaQueryEntity> {

    private static final Map<String, CampoSpec<ObservacionItemAsesorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<ObservacionItemAsesorJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : ObservacionItemAsesorCriteria.Campo.values()) {
            CampoSpec<ObservacionItemAsesorJpaQueryEntity> spec = switch (campo) {
                case DESCRIPCION -> CampoSpec.texto(root -> root.get("descripcion"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<ObservacionItemAsesorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }

    Specification<ObservacionItemAsesorJpaQueryEntity> deLaEvaluacion(UUID evaluacionCuantitativaAsesor) {
        return (root, query, cb) -> cb.equal(root.get("evaluacionCuantitativaAsesorId"), evaluacionCuantitativaAsesor);
    }
}
