package com.arquisoft.fichas.infrastructure.fichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.fichaperfil.query.criteria.FichaPerfilCriteria;
import com.arquisoft.shared.postgres.query.CampoSpec;
import com.arquisoft.shared.postgres.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class FichaPerfilJpaSpecification extends QueryJpaSpecification<FichaPerfilJpaQueryEntity> {

    private static final Map<String, CampoSpec<FichaPerfilJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<FichaPerfilJpaQueryEntity>> m = new LinkedHashMap<>();
        for (FichaPerfilCriteria.Campo campo : FichaPerfilCriteria.Campo.values()) {
            CampoSpec<FichaPerfilJpaQueryEntity> spec = switch (campo) {
                case TITULO_PROYECTO -> CampoSpec.texto(root -> root.get("tituloProyecto"));
                case ASESOR_NOMBRE   -> CampoSpec.texto(root -> root.get("asesorNombre"));
                case ASESOR_EMAIL    -> CampoSpec.texto(root -> root.get("asesorEmail"));
                case ASESOR_ID       -> CampoSpec.uuid(root -> root.get("asesorId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<FichaPerfilJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
