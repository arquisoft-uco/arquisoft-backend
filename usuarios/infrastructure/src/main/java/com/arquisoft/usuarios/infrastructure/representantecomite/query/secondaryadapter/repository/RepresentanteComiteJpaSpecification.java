package com.arquisoft.usuarios.infrastructure.representantecomite.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.representantecomite.query.criteria.RepresentanteComiteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class RepresentanteComiteJpaSpecification extends QueryJpaSpecification<RepresentanteComiteJpaQueryEntity> {

    private static final Map<String, CampoSpec<RepresentanteComiteJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<RepresentanteComiteJpaQueryEntity>> m = new LinkedHashMap<>();
        for (RepresentanteComiteCriteria.Campo campo : RepresentanteComiteCriteria.Campo.values()) {
            CampoSpec<RepresentanteComiteJpaQueryEntity> spec = switch (campo) {
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
    protected Map<String, CampoSpec<RepresentanteComiteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
