package com.arquisoft.usuarios.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.estudiante.query.criteria.EstudianteVigenteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class EstudianteVigenteJpaSpecification extends QueryJpaSpecification<EstudianteVigenteJpaQueryEntity> {

    private static final Map<String, CampoSpec<EstudianteVigenteJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<EstudianteVigenteJpaQueryEntity>> m = new LinkedHashMap<>();
        for (EstudianteVigenteCriteria.Campo campo : EstudianteVigenteCriteria.Campo.values()) {
            CampoSpec<EstudianteVigenteJpaQueryEntity> spec = switch (campo) {
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
    protected Map<String, CampoSpec<EstudianteVigenteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
