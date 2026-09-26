package com.arquisoft.usuarios.application.asesor.command.primaryport.mapper;

import com.arquisoft.usuarios.application.asesor.command.primaryport.model.RemoverAsesorCommand;
import com.arquisoft.usuarios.domain.asesor.AsesorDomain;

public final class RemoverAsesorMapper {

    private RemoverAsesorMapper() {}

    public static AsesorDomain toDomain(RemoverAsesorCommand command) {
        return AsesorDomain.crear(command.usuario());
    }
}
