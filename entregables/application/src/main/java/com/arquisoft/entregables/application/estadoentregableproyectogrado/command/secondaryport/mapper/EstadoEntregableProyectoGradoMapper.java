package com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.mapper;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.entity.EstadoEntregableProyectoGradoEntity;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;

public final class EstadoEntregableProyectoGradoMapper {

    private EstadoEntregableProyectoGradoMapper() {}

    public static EstadoEntregableProyectoGradoEntity toEntity(EstadoEntregableProyectoGradoDomain estado) {
        return new EstadoEntregableProyectoGradoEntity(
                estado.getId(),
                estado.getEntregableProyectoGrado(),
                estado.getEstadoEntregable().getId(),
                estado.getFechaActualizacion());
    }

    public static EstadoEntregableProyectoGradoDomain toDomain(EstadoEntregableProyectoGradoEntity entity) {
        return EstadoEntregableProyectoGradoDomain.reconstruir(
                entity.id(),
                entity.entregableProyectoGrado(),
                EstadoEntregable.desde(entity.estadoEntregable()),
                entity.fechaActualizacion());
    }
}
