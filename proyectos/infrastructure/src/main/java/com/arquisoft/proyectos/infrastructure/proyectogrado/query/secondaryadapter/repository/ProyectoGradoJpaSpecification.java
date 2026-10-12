package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class ProyectoGradoJpaSpecification extends QueryJpaSpecification<ProyectoGradoListadoJpaQueryEntity> {

    private static final Map<String, CampoSpec<ProyectoGradoListadoJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<ProyectoGradoListadoJpaQueryEntity>> m = new LinkedHashMap<>();
        for (ProyectoGradoCriteria.Campo campo : ProyectoGradoCriteria.Campo.values()) {
            CampoSpec<ProyectoGradoListadoJpaQueryEntity> spec = switch (campo) {
                case ID                    -> CampoSpec.uuid(root -> root.get("id"));
                case TITULO_PROYECTO       -> CampoSpec.texto(root -> root.get("tituloProyecto"));
                case ESTADO_PROYECTO_GRADO -> CampoSpec.texto(root -> root.get("estadoId"));
                case COORDINADOR           -> CampoSpec.uuid(root -> root.get("coordinadorId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<ProyectoGradoListadoJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
