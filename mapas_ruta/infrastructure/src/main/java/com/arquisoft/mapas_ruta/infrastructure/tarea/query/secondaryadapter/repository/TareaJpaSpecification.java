package com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaMapaRutaCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Component
class TareaJpaSpecification extends QueryJpaSpecification<TareaJpaQueryEntity> {

    private static final Map<String, CampoSpec<TareaJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<TareaJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : TareaMapaRutaCriteria.Campo.values()) {
            CampoSpec<TareaJpaQueryEntity> spec = switch (campo) {
                case DESCRIPCION -> CampoSpec.texto(root -> root.get("descripcion"));
                case ESTADO_ITEM -> CampoSpec.texto(root -> root.get("estadoItemId"));
                case ASESOR      -> CampoSpec.uuid(root -> root.get("asesorId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<TareaJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }

    Specification<TareaJpaQueryEntity> deLaMapaRuta(UUID mapaRuta) {
        return (root, query, cb) -> cb.equal(root.get("mapaRutaId"), mapaRuta);
    }
}
