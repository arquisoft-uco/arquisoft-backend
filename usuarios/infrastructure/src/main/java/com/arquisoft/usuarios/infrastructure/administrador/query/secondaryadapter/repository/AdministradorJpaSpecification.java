package com.arquisoft.usuarios.infrastructure.administrador.query.secondaryadapter.repository;

import com.arquisoft.usuarios.application.administrador.query.criteria.AdministradorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class AdministradorJpaSpecification extends QueryJpaSpecification<AdministradorJpaQueryEntity> {

    private static final Map<String, CampoSpec<AdministradorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<AdministradorJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : AdministradorCriteria.Campo.values()) {
            CampoSpec<AdministradorJpaQueryEntity> spec = switch (campo) {
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
    protected Map<String, CampoSpec<AdministradorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
