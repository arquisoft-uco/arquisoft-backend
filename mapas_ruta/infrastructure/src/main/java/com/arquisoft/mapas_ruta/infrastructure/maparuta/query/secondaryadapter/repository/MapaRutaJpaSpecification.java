package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.secondaryadapter.repository;

import com.arquisoft.mapas_ruta.application.maparuta.query.criteria.MapaRutaCriteria;
import com.arquisoft.shared.jpa.query.CampoSpec;
import com.arquisoft.shared.jpa.query.QueryJpaSpecification;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
class MapaRutaJpaSpecification extends QueryJpaSpecification<MapaRutaJpaQueryEntity> {

    private static final Map<String, CampoSpec<MapaRutaJpaQueryEntity>> CAMPOS;

    static {
        Map<String, CampoSpec<MapaRutaJpaQueryEntity>> m = new LinkedHashMap<>();
        for (var campo : MapaRutaCriteria.Campo.values()) {
            CampoSpec<MapaRutaJpaQueryEntity> spec = switch (campo) {
                case FECHA_INICIO -> CampoSpec.fecha(root -> root.get("fechaInicio"));
                case FECHA_FIN    -> CampoSpec.fecha(root -> root.get("fechaFin"));
                case COORDINADOR  -> CampoSpec.uuid(root -> root.get("coordinadorId"));
            };
            m.put(campo.getClave(), spec);
        }
        CAMPOS = Collections.unmodifiableMap(m);
    }

    @Override
    protected Map<String, CampoSpec<MapaRutaJpaQueryEntity>> camposPermitidos() {
        return CAMPOS;
    }
}
