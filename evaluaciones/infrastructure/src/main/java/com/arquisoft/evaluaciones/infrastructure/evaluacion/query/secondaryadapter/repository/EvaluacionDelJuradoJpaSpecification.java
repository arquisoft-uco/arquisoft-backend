package com.arquisoft.evaluaciones.infrastructure.evaluacion.query.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.query.criteria.EvaluacionDelJuradoCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class EvaluacionDelJuradoJpaSpecification extends QueryJpaSpecification<EvaluacionDelJuradoJpaQueryEntity> {

    private static final Map<String, CampoSpec<EvaluacionDelJuradoJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<EvaluacionDelJuradoJpaQueryEntity>> m = new LinkedHashMap<>();
        for (EvaluacionDelJuradoCriteria.Campo campo : EvaluacionDelJuradoCriteria.Campo.values()) {
            CampoSpec<EvaluacionDelJuradoJpaQueryEntity> spec = switch (campo) {
                case ESTADO -> CampoSpec.texto(root -> root.get("estadoId"));
                case PROYECTO -> CampoSpec.texto(root -> root.get("entregableProyecto"));
                case ENTREGABLE_ID -> CampoSpec.uuid(root -> root.get("entregableId"));
                case JURADO_ID -> CampoSpec.uuid(root -> root.get("juradoId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<EvaluacionDelJuradoJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
