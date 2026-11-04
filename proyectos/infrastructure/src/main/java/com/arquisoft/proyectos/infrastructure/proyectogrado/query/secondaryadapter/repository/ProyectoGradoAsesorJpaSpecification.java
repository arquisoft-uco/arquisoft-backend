package com.arquisoft.proyectos.infrastructure.proyectogrado.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.proyectogrado.query.criteria.ProyectoGradoAsesorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class ProyectoGradoAsesorJpaSpecification extends QueryJpaSpecification<ProyectoGradoAsesorJpaQueryEntity> {

    private static final Map<String, CampoSpec<ProyectoGradoAsesorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<ProyectoGradoAsesorJpaQueryEntity>> m = new LinkedHashMap<>();
        for (ProyectoGradoAsesorCriteria.Campo campo : ProyectoGradoAsesorCriteria.Campo.values()) {
            CampoSpec<ProyectoGradoAsesorJpaQueryEntity> spec = switch (campo) {
                case TITULO_PROYECTO       -> CampoSpec.texto(root -> root.get("tituloProyecto"));
                case ESTADO_PROYECTO_GRADO -> CampoSpec.texto(root -> root.get("estadoId"));
                case ASESOR                -> CampoSpec.uuid(root -> root.get("asesorId"));
                case ESTADO_ASESOR         -> CampoSpec.texto(root -> root.get("estadoAsesorId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<ProyectoGradoAsesorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
