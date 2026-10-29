package com.arquisoft.proyectos.infrastructure.estadoasesor.query.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estadoasesor.query.criteria.EstadoAsesorCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class EstadoAsesorJpaSpecification extends QueryJpaSpecification<EstadoAsesorJpaQueryEntity> {

    private static final Map<String, CampoSpec<EstadoAsesorJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<EstadoAsesorJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : EstadoAsesorCriteria.Campo.values()) {
            CampoSpec<EstadoAsesorJpaQueryEntity> spec = switch (campo) {
                case ID     -> CampoSpec.texto(root -> root.get("id"));
                case NOMBRE -> CampoSpec.texto(root -> root.get("nombre"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<EstadoAsesorJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
