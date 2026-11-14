package com.arquisoft.evaluaciones.application.entregable.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.entregable.command.secondaryport.entity.EntregableEntity;
import com.arquisoft.evaluaciones.domain.entregable.EntregableDomain;

public final class EntregableMapper {

    private EntregableMapper() {}

    public static EntregableEntity toEntity(EntregableDomain entregable) {
        return new EntregableEntity(
                entregable.getId(), entregable.getProyectoGrado(), entregable.getProyecto(),
                entregable.getVersion());
    }
}
