package com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.query.readmodel.MapaRutaEstudianteReadModel;
import com.arquisoft.mapas_ruta.infrastructure.maparuta.query.primaryadapter.web.dto.MapaRutaEstudianteResponseDTO;

public final class MapaRutaEstudianteResponseMapper {

    private MapaRutaEstudianteResponseMapper() {}

    public static MapaRutaEstudianteResponseDTO toResponse(MapaRutaEstudianteReadModel r) {
        return new MapaRutaEstudianteResponseDTO(
                r.id(),
                r.proyectoGrado(),
                r.tituloProyecto(),
                r.fechaInicio(),
                r.fechaFin());
    }
}
