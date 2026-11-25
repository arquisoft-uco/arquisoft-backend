package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilRepresentanteCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
class FichaPerfilRepresentanteJpaSpecification
        extends QueryJpaSpecification<FichaPerfilRepresentanteJpaQueryEntity> {

    private static final Map<String, CampoSpec<FichaPerfilRepresentanteJpaQueryEntity>> CAMPOS;

    static {
        var m = new LinkedHashMap<String, CampoSpec<FichaPerfilRepresentanteJpaQueryEntity>>();
        for (var campo : FichaPerfilRepresentanteCriteria.Campo.values()) {
            CampoSpec<FichaPerfilRepresentanteJpaQueryEntity> spec = switch (campo) {
                case TITULO_PROYECTO -> CampoSpec.texto(root -> root.get("tituloProyecto"));
                case ASESOR_NOMBRE   -> CampoSpec.texto(root -> root.get("asesorNombre"));
                case ASESOR_EMAIL    -> CampoSpec.texto(root -> root.get("asesorEmail"));
                case ESTADO_FICHA    -> CampoSpec.texto(root -> root.get("estadoId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<FichaPerfilRepresentanteJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }

    Specification<FichaPerfilRepresentanteJpaQueryEntity> delRepresentante(UUID representanteComite) {
        return (root, query, cb) -> cb.equal(root.get("representanteComiteId"), representanteComite);
    }
}
