package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.mapper;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.entity.EstadoEntregableProyectoGradoAccesoEntity;
import com.arquisoft.entregables.domain.estadoentregable.EstadoEntregable;
import com.arquisoft.entregables.domain.estadoentregableproyectogrado.EstadoEntregableProyectoGradoDomain;

public final class EstadoEntregableProyectoGradoAccesoMapper {

    private EstadoEntregableProyectoGradoAccesoMapper() {}

    public static EstadoEntregableProyectoGradoDomain toDomain(EstadoEntregableProyectoGradoAccesoEntity entity) {
        return EstadoEntregableProyectoGradoDomain.reconstruir(
                entity.id(),
                entity.entregableProyectoGrado(),
                EstadoEntregable.desde(entity.estadoEntregable()),
                entity.fechaActualizacion());
    }
}
