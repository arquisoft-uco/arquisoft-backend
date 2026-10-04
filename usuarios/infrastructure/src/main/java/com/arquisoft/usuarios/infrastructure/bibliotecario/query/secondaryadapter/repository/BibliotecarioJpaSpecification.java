package com.arquisoft.usuarios.infrastructure.bibliotecario.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.bibliotecario.query.criteria.BibliotecarioCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class BibliotecarioJpaSpecification extends QueryJpaSpecification<BibliotecarioJpaQueryEntity> {

    private static final Map<String, CampoSpec<BibliotecarioJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<BibliotecarioJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : BibliotecarioCriteria.Campo.values()) {
            CampoSpec<BibliotecarioJpaQueryEntity> spec = switch (campo) {
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
    protected Map<String, CampoSpec<BibliotecarioJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
