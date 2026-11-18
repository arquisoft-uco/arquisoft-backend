package com.arquisoft.mapas_ruta.infrastructure.planreal.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planreal.query.criteria.PlanRealProyectoCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
class PlanRealProyectoJpaSpecification extends QueryJpaSpecification<PlanRealProyectoJpaQueryEntity> {

    private static final Map<String, CampoSpec<PlanRealProyectoJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<PlanRealProyectoJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : PlanRealProyectoCriteria.Campo.values()) {
            CampoSpec<PlanRealProyectoJpaQueryEntity> spec = switch (campo) {
                case TIEMPO_REAL_DIAS -> CampoSpec.entero(root -> root.get("tiempoRealDias"));
                case DESCRIPCION_REAL -> CampoSpec.texto(root -> root.get("descripcionReal"));
                case DESCRIPCION_TAREA -> CampoSpec.texto(root -> root.get("descripcionTarea"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<PlanRealProyectoJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }

    Specification<PlanRealProyectoJpaQueryEntity> delProyectoGrado(UUID proyectoGrado) {
        return (root, query, cb) -> cb.equal(root.get("proyectoGradoId"), proyectoGrado);
    }
}
