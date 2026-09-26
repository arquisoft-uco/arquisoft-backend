package com.arquisoft.usuarios.infrastructure.asesorficha.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.asesorficha.query.criteria.AsesorFichaCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class AsesorFichaJpaSpecification extends QueryJpaSpecification<AsesorFichaJpaQueryEntity> {

    private static final Map<String, CampoSpec<AsesorFichaJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<AsesorFichaJpaQueryEntity>> m = new LinkedHashMap<>();
        for (AsesorFichaCriteria.Campo campo : AsesorFichaCriteria.Campo.values()) {
            CampoSpec<AsesorFichaJpaQueryEntity> spec = switch (campo) {
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
    protected Map<String, CampoSpec<AsesorFichaJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
