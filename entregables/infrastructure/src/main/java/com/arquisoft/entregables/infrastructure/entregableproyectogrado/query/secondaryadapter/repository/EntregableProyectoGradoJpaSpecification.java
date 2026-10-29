package com.arquisoft.entregables.infrastructure.entregableproyectogrado.query.secondaryadapter.repository;

import com.arquisoft.entregables.application.entregableproyectogrado.query.criteria.EntregableProyectoGradoCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class EntregableProyectoGradoJpaSpecification extends QueryJpaSpecification<EntregableProyectoGradoJpaQueryEntity> {

    private static final Map<String, CampoSpec<EntregableProyectoGradoJpaQueryEntity>> CAMPOS;

    static {
        var m = new LinkedHashMap<String, CampoSpec<EntregableProyectoGradoJpaQueryEntity>>();
        for (var campo : EntregableProyectoGradoCriteria.Campo.values()) {
            CampoSpec<EntregableProyectoGradoJpaQueryEntity> spec = switch (campo) {
                case PROYECTO_GRADO_ID -> CampoSpec.uuid(root -> root.get("proyectoGradoId"));
                case PROYECTO_GRADO_NOMBRE -> CampoSpec.texto(root -> root.get("proyectoGradoNombre"));
                case ASESOR_ID -> CampoSpec.uuid(root -> root.get("asesorId"));
                case ASESOR_NOMBRE -> CampoSpec.texto(root -> root.get("asesorNombre"));
                case ESTADO_ENTREGABLE -> CampoSpec.texto(root -> root.get("estadoId"));
                case VERSION_ENTREGABLE -> CampoSpec.entero(root -> root.get("versionEntregable"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<EntregableProyectoGradoJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
