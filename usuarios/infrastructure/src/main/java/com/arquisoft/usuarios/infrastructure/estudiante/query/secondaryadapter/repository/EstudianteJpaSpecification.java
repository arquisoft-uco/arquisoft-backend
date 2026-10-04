package com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class EstudianteJpaSpecification extends QueryJpaSpecification<EstudianteJpaQueryEntity> {

    private static final Map<String, CampoSpec<EstudianteJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<EstudianteJpaQueryEntity>> m = new LinkedHashMap<>();
        for (EstudianteCriteria.Campo campo : EstudianteCriteria.Campo.values()) {
            CampoSpec<EstudianteJpaQueryEntity> spec = switch (campo) {
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
    protected Map<String, CampoSpec<EstudianteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
