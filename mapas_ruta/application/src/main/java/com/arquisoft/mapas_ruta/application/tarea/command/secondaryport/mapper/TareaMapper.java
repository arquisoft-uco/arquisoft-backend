package com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.mapper;

import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.entity.TareaEntity;
import com.arquisoft.mapas_ruta.domain.tarea.TareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;

public final class TareaMapper {

    private TareaMapper() {}

    public static TareaEntity toEntity(TareaDomain domain) {
        return new TareaEntity(
                domain.getId(),
                domain.getMapaRuta(),
                domain.getDescripcion(),
                domain.getAsesor(),
                domain.getEstadoItem().getId());
    }

    public static TareaDomain toDomain(TareaEntity entity) {
        return TareaDomain.reconstruir(
                entity.id(),
                entity.mapaRuta(),
                entity.descripcion(),
                entity.asesor(),
                EstadoItem.desde(entity.estadoItem()));
    }
}
