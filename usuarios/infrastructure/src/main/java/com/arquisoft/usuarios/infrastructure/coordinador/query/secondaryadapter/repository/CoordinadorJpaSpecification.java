package com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class CoordinadorJpaSpecification extends QueryJpaSpecification<CoordinadorJpaQueryEntity> {

    private static final Map<String, CampoSpec<CoordinadorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<CoordinadorJpaQueryEntity>> m = new LinkedHashMap<>();
        for (CoordinadorCriteria.Campo campo : CoordinadorCriteria.Campo.values()) {
            CampoSpec<CoordinadorJpaQueryEntity> spec = switch (campo) {
                case IDENTIFICADOR -> CampoSpec.texto(root -> root.get("identificador"));
                case NOMBRE        -> CampoSpec.texto(root -> root.get("nombre"));
                case EMAIL         -> CampoSpec.texto(root -> root.get("email"));
                case ESTADO        -> CampoSpec.texto(root -> root.get("estado"));
                case VIGENTE       -> CampoSpec.booleano(root -> root.get("vigente"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<CoordinadorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
