package com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealTareaCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
class PlanRealJpaSpecification extends QueryJpaSpecification<PlanRealJpaQueryEntity> {

    private static final Map<String, CampoSpec<PlanRealJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<PlanRealJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : PlanRealTareaCriteria.Campo.values()) {
            CampoSpec<PlanRealJpaQueryEntity> spec = switch (campo) {
                case TIEMPO_REAL_DIAS -> CampoSpec.entero(root -> root.get("tiempoRealDias"));
                case DESCRIPCION_REAL -> CampoSpec.texto(root -> root.get("descripcionReal"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<PlanRealJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }

    Specification<PlanRealJpaQueryEntity> deLaTarea(UUID tarea) {
        return (root, query, cb) -> cb.equal(root.get("tareaId"), tarea);
    }
}
