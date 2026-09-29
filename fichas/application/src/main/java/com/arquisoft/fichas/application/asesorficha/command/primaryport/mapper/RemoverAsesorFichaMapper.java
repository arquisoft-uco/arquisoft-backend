package com.arquisoft.fichas.application.asesorficha.command.primaryport.mapper;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.RemoverAsesorFichaCommand;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;

public final class RemoverAsesorFichaMapper {

    private RemoverAsesorFichaMapper() {}

    public static AsesorFichaDomain toDomain(RemoverAsesorFichaCommand command) {
        return AsesorFichaDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
