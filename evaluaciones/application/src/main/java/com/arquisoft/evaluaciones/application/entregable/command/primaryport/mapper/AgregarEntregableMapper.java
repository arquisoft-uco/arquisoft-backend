package com.arquisoft.evaluaciones.application.entregable.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.entregable.command.primaryport.model.AgregarEntregableCommand;
import com.arquisoft.evaluaciones.domain.entregable.EntregableDomain;

public final class AgregarEntregableMapper {

    private AgregarEntregableMapper() {}

    public static EntregableDomain toDomain(AgregarEntregableCommand command) {
        return EntregableDomain.crear(
                command.entregable(), command.proyectoGrado(), command.proyecto(), command.version());
    }
}
