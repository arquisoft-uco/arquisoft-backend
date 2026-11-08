package com.arquisoft.mapas_ruta.infrastructure.planestimado.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.planestimado.query.criteria.PlanEstimadoProyectoCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
class PlanEstimadoProyectoJpaSpecification extends QueryJpaSpecification<PlanEstimadoProyectoJpaQueryEntity> {

    private static final Map<String, CampoSpec<PlanEstimadoProyectoJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<PlanEstimadoProyectoJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : PlanEstimadoProyectoCriteria.Campo.values()) {
            CampoSpec<PlanEstimadoProyectoJpaQueryEntity> spec = switch (campo) {
                case TIEMPO_ESTIMADO_DIAS -> CampoSpec.entero(root -> root.get("tiempoEstimadoDias"));
                case DESCRIPCION_ESTIMADO -> CampoSpec.texto(root -> root.get("descripcionEstimado"));
                case DESCRIPCION_TAREA -> CampoSpec.texto(root -> root.get("descripcionTarea"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<PlanEstimadoProyectoJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }

    Specification<PlanEstimadoProyectoJpaQueryEntity> delProyectoGrado(UUID proyectoGrado) {
        return (root, query, cb) -> cb.equal(root.get("proyectoGradoId"), proyectoGrado);
    }
}
