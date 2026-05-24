package com.arquisoft.fichas.infrastructure.fichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilJpaEntity;
import com.arquisoft.shared.postgres.query.CampoSpec;
import com.arquisoft.shared.postgres.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Implementa en JPA el contrato de campos filtrables declarado en FichaPerfilCriteria.Campo.
 *
 * El switch exhaustivo garantiza que todo campo del enum tenga una traducción JPA.
 * Si se agrega un campo al enum sin implementarlo aquí, el compilador lo detecta.
 */
@Component
class FichaPerfilJpaSpecification extends QueryJpaSpecification<FichaPerfilJpaEntity> {

    private static final Map<String, CampoSpec<FichaPerfilJpaEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<FichaPerfilJpaEntity>> m = new LinkedHashMap<>();
        for (FichaPerfilCriteria.Campo campo : FichaPerfilCriteria.Campo.values()) {
            CampoSpec<FichaPerfilJpaEntity> spec = switch (campo) {
                case TITULO_PROYECTO -> CampoSpec.texto(root -> root.get("tituloProyecto"));
                case ASESOR_NOMBRE   -> CampoSpec.texto(root -> root.get("asesorFicha").get("nombre"));
                case ASESOR_EMAIL    -> CampoSpec.texto(root -> root.get("asesorFicha").get("email"));
                case ASESOR_ID       -> CampoSpec.uuid(root -> root.get("asesorFicha").get("id"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<FichaPerfilJpaEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
