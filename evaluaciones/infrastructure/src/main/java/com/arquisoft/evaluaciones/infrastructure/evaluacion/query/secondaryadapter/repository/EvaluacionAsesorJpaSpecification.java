package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionAsesorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class EvaluacionAsesorJpaSpecification extends QueryJpaSpecification<EvaluacionAsesorJpaQueryEntity> {

    private static final Map<String, CampoSpec<EvaluacionAsesorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<EvaluacionAsesorJpaQueryEntity>> m = new LinkedHashMap<>();
        for (EvaluacionAsesorCriteria.Campo campo : EvaluacionAsesorCriteria.Campo.values()) {
            CampoSpec<EvaluacionAsesorJpaQueryEntity> spec = switch (campo) {
                case ESTADO -> CampoSpec.texto(root -> root.get("estadoId"));
                case PROYECTO -> CampoSpec.texto(root -> root.get("entregableProyecto"));
                case ENTREGABLE_ID -> CampoSpec.uuid(root -> root.get("entregableId"));
                case ASESOR_ID -> CampoSpec.uuid(root -> root.get("asesorId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<EvaluacionAsesorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
