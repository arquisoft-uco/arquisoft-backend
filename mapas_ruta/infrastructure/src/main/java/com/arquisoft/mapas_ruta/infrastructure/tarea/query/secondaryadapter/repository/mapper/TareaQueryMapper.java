package com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository.mapper;

import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.infrastructure.tarea.query.secondaryadapter.repository.TareaJpaQueryEntity;

public final class TareaQueryMapper {

    private TareaQueryMapper() {}

    public static TareaReadModel toReadModel(TareaJpaQueryEntity entity) {
        return new TareaReadModel(
                entity.getId(),
                entity.getDescripcion(),
                entity.getMapaRutaId(),
                entity.getFechaInicioMapaRuta(),
                entity.getFechaFinMapaRuta(),
                entity.getEstadoItemId(),
                entity.getNombreEstadoItem(),
                entity.getAsesorId(),
                entity.getNombreAsesor(),
                entity.getEmailAsesor());
    }
}
