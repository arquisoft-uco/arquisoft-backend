package com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.mapper;

import com.arquisoft.mapas_ruta.application.maparuta.command.secondaryport.entity.MapaRutaEntity;
import com.arquisoft.mapas_ruta.domain.maparuta.MapaRutaDomain;

public final class MapaRutaMapper {

    private MapaRutaMapper() {}

    public static MapaRutaDomain toDomain(MapaRutaEntity entity) {
        return MapaRutaDomain.reconstruir(entity.id(), entity.proyectoGrado(), entity.fechaInicio(), entity.fechaFin());
    }

    public static MapaRutaEntity toEntity(MapaRutaDomain domain) {
        return new MapaRutaEntity(domain.getId(), domain.getProyectoGrado(), domain.getFechaInicio(), domain.getFechaFin());
    }
}
