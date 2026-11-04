package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaReadModel;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.dto.MapaRutaResponseDTO;

public final class MapaRutaResponseMapper {

    private MapaRutaResponseMapper() {}

    public static MapaRutaResponseDTO toResponse(MapaRutaReadModel r) {
        return new MapaRutaResponseDTO(
                r.id(),
                r.proyectoGrado(),
                r.tituloProyecto(),
                r.fechaInicio(),
                r.fechaFin());
    }
}
