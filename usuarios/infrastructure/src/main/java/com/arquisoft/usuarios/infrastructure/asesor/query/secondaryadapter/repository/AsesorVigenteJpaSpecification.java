package com.arquisoft.usuarios.infrastructure.asesor.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorVigenteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class AsesorVigenteJpaSpecification extends QueryJpaSpecification<AsesorVigenteJpaQueryEntity> {

    private static final Map<String, CampoSpec<AsesorVigenteJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<AsesorVigenteJpaQueryEntity>> m = new LinkedHashMap<>();
        for (AsesorVigenteCriteria.Campo campo : AsesorVigenteCriteria.Campo.values()) {
            CampoSpec<AsesorVigenteJpaQueryEntity> spec = switch (campo) {
                case IDENTIFICADOR -> CampoSpec.texto(root -> root.get("identificador"));
                case NOMBRE        -> CampoSpec.texto(root -> root.get("nombre"));
                case EMAIL         -> CampoSpec.texto(root -> root.get("email"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<AsesorVigenteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
