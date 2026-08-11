package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.fichas.infrastructure.fichaperfil.persistence.FichaPerfilEntity;
import com.arquisoft.shared.postgres.query.CampoSpec;
import com.arquisoft.shared.postgres.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class FichaPerfilJpaSpecification extends QueryJpaSpecification<FichaPerfilEntity> {

    private static final Map<String, CampoSpec<FichaPerfilEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<FichaPerfilEntity>> m = new LinkedHashMap<>();
        for (FichaPerfilCriteria.Campo campo : FichaPerfilCriteria.Campo.values()) {
            CampoSpec<FichaPerfilEntity> spec = switch (campo) {
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
    protected Map<String, CampoSpec<FichaPerfilEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
