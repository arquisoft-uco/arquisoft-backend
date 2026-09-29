package com.arquisoft.usuarios.application.asesorficha.command.primaryport.mapper;

import com.arquisoft.usuarios.application.asesorficha.command.primaryport.model.RemoverAsesorFichaCommand;
import com.arquisoft.usuarios.domain.asesorficha.AsesorFichaDomain;

public final class RemoverAsesorFichaMapper {

    private RemoverAsesorFichaMapper() {}

    public static AsesorFichaDomain toDomain(RemoverAsesorFichaCommand command) {
        return AsesorFichaDomain.crear(command.usuario());
    }
}
