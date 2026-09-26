package com.arquisoft.usuarios.infrastructure.asesor.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesor.query.criteria.AsesorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class AsesorJpaSpecification extends QueryJpaSpecification<AsesorJpaQueryEntity> {

    private static final Map<String, CampoSpec<AsesorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<AsesorJpaQueryEntity>> m = new LinkedHashMap<>();
        for (AsesorCriteria.Campo campo : AsesorCriteria.Campo.values()) {
            CampoSpec<AsesorJpaQueryEntity> spec = switch (campo) {
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
    protected Map<String, CampoSpec<AsesorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
