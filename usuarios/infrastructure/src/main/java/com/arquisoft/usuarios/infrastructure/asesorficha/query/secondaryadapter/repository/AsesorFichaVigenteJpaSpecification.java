package com.arquisoft.usuarios.infrastructure.asesorficha.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaVigenteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class AsesorFichaVigenteJpaSpecification extends QueryJpaSpecification<AsesorFichaVigenteJpaQueryEntity> {

    private static final Map<String, CampoSpec<AsesorFichaVigenteJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<AsesorFichaVigenteJpaQueryEntity>> m = new LinkedHashMap<>();
        for (AsesorFichaVigenteCriteria.Campo campo : AsesorFichaVigenteCriteria.Campo.values()) {
            CampoSpec<AsesorFichaVigenteJpaQueryEntity> spec = switch (campo) {
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
    protected Map<String, CampoSpec<AsesorFichaVigenteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
