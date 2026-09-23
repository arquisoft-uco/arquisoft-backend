package com.arquisoft.fichas.application.asesorficha.command.primaryport.mapper;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.ActualizarAsesorFichaCommand;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;

public final class ActualizarAsesorFichaMapper {

    private ActualizarAsesorFichaMapper() {}

    public static AsesorFichaDomain toDomain(ActualizarAsesorFichaCommand command) {
        return AsesorFichaDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
