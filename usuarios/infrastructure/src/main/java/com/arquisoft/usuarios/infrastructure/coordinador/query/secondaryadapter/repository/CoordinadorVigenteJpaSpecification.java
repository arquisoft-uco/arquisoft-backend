package com.arquisoft.usuarios.infrastructure.coordinador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.coordinador.query.criteria.CoordinadorVigenteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class CoordinadorVigenteJpaSpecification extends QueryJpaSpecification<CoordinadorVigenteJpaQueryEntity> {

    private static final Map<String, CampoSpec<CoordinadorVigenteJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<CoordinadorVigenteJpaQueryEntity>> m = new LinkedHashMap<>();
        for (CoordinadorVigenteCriteria.Campo campo : CoordinadorVigenteCriteria.Campo.values()) {
            CampoSpec<CoordinadorVigenteJpaQueryEntity> spec = switch (campo) {
                case IDENTIFICADOR -> CampoSpec.texto(root -> root.get("identificador"));
                case NOMBRE        -> CampoSpec.texto(root -> root.get("nombre"));
                case EMAIL         -> CampoSpec.texto(root -> root.get("email"));
                case ESTADO        -> CampoSpec.texto(root -> root.get("estado"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<CoordinadorVigenteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
