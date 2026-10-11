package com.arquisoft.fichas.infrastructure.observacionitem.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.observacionitem.query.criteria.ObservacionItemEstudianteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class ObservacionItemEstudianteJpaSpecification extends QueryJpaSpecification<ObservacionItemEstudianteJpaQueryEntity> {

    private static final Map<String, CampoSpec<ObservacionItemEstudianteJpaQueryEntity>> CAMPOS;

    static {
        var m = new LinkedHashMap<String, CampoSpec<ObservacionItemEstudianteJpaQueryEntity>>();
        for (var campo : ObservacionItemEstudianteCriteria.Campo.values()) {
            CampoSpec<ObservacionItemEstudianteJpaQueryEntity> spec = switch (campo) {
                case REVISION_ITEM               -> CampoSpec.uuid(root -> root.get("revisionItemId"));
                case ESTADO_OBSERVACION_REVISION -> CampoSpec.texto(root -> root.get("estadoObservacionRevisionId"));
                case ESTUDIANTE_ID               -> CampoSpec.uuid(root -> root.get("estudianteId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<ObservacionItemEstudianteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
