package com.arquisoft.usuarios.infrastructure.representantecomite.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteVigenteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class RepresentanteComiteVigenteJpaSpecification extends QueryJpaSpecification<RepresentanteComiteVigenteJpaQueryEntity> {

    private static final Map<String, CampoSpec<RepresentanteComiteVigenteJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<RepresentanteComiteVigenteJpaQueryEntity>> m = new LinkedHashMap<>();
        for (RepresentanteComiteVigenteCriteria.Campo campo : RepresentanteComiteVigenteCriteria.Campo.values()) {
            CampoSpec<RepresentanteComiteVigenteJpaQueryEntity> spec = switch (campo) {
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
    protected Map<String, CampoSpec<RepresentanteComiteVigenteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
