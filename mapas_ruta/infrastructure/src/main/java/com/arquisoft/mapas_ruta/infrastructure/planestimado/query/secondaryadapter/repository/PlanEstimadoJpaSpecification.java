package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoTareaCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
class PlanEstimadoJpaSpecification extends QueryJpaSpecification<PlanEstimadoJpaQueryEntity> {

    private static final Map<String, CampoSpec<PlanEstimadoJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<PlanEstimadoJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : PlanEstimadoTareaCriteria.Campo.values()) {
            CampoSpec<PlanEstimadoJpaQueryEntity> spec = switch (campo) {
                case TIEMPO_ESTIMADO_DIAS -> CampoSpec.entero(root -> root.get("tiempoEstimadoDias"));
                case DESCRIPCION_ESTIMADO -> CampoSpec.texto(root -> root.get("descripcionEstimado"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<PlanEstimadoJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }

    Specification<PlanEstimadoJpaQueryEntity> deLaTarea(UUID tarea) {
        return (root, query, cb) -> cb.equal(root.get("tareaId"), tarea);
    }
}
