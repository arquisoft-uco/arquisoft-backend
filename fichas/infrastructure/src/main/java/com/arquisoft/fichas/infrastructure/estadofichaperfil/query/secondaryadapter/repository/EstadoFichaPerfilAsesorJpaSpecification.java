package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.query.criteria.EstadoFichaPerfilAsesorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class EstadoFichaPerfilAsesorJpaSpecification extends QueryJpaSpecification<EstadoFichaPerfilAsesorJpaQueryEntity> {

    private static final Map<String, CampoSpec<EstadoFichaPerfilAsesorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<EstadoFichaPerfilAsesorJpaQueryEntity>> m = new LinkedHashMap<>();
        for (EstadoFichaPerfilAsesorCriteria.Campo campo : EstadoFichaPerfilAsesorCriteria.Campo.values()) {
            CampoSpec<EstadoFichaPerfilAsesorJpaQueryEntity> spec = switch (campo) {
                case FICHA_PERFIL    -> CampoSpec.uuid(root -> root.get("fichaPerfilId"));
                case TITULO_PROYECTO -> CampoSpec.texto(root -> root.get("tituloProyecto"));
                case ESTADO_FICHA    -> CampoSpec.texto(root -> root.get("estadoId"));
                case ASESOR_FICHA    -> CampoSpec.uuid(root -> root.get("asesorFichaId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<EstadoFichaPerfilAsesorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
