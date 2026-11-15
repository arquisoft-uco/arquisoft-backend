package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionasesor.query.criteria.EvaluacionesAsignadasAsesorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
class EvaluacionAsignadaAsesorJpaSpecification
        extends QueryJpaSpecification<EvaluacionAsignadaAsesorJpaQueryEntity> {

    private static final Map<String, CampoSpec<EvaluacionAsignadaAsesorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<EvaluacionAsignadaAsesorJpaQueryEntity>> m = new LinkedHashMap<>();
        m.put(EvaluacionesAsignadasAsesorCriteria.Campo.ESTADO.getClave(),
                CampoSpec.texto(root -> root.get("estadoId")));
        m.put(EvaluacionesAsignadasAsesorCriteria.Campo.PROYECTO.getClave(),
                CampoSpec.texto(root -> root.get("entregableProyecto")));
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<EvaluacionAsignadaAsesorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }

    Specification<EvaluacionAsignadaAsesorJpaQueryEntity> delAsesor(UUID asesor) {
        return (root, query, cb) -> cb.equal(root.get("asesorId"), asesor);
    }
}
