package com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.mapper;

import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.entregables.domain.entregableproyectogrado.EntregableProyectoGradoDomain;

public final class EntregableProyectoGradoMapper {

    private EntregableProyectoGradoMapper() {}

    public static EntregableProyectoGradoEntity toEntity(EntregableProyectoGradoDomain entregable) {
        return new EntregableProyectoGradoEntity(
                entregable.getId(),
                entregable.getProyectoGrado(),
                entregable.getVersion(),
                entregable.getAsesor(),
                entregable.getEstadoEntregable().getId());
    }
}
