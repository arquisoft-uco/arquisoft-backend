package com.arquisoft.usuarios.infrastructure.jurado.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.jurado.query.criteria.JuradoVigenteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class JuradoVigenteJpaSpecification extends QueryJpaSpecification<JuradoVigenteJpaQueryEntity> {

    private static final Map<String, CampoSpec<JuradoVigenteJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<JuradoVigenteJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : JuradoVigenteCriteria.Campo.values()) {
            CampoSpec<JuradoVigenteJpaQueryEntity> spec = switch (campo) {
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
    protected Map<String, CampoSpec<JuradoVigenteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
