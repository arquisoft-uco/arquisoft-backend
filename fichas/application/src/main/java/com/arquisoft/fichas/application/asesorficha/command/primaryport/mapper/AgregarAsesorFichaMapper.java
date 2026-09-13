package com.arquisoft.fichas.application.asesorficha.command.primaryport.mapper;

import com.arquisoft.fichas.application.asesorficha.command.primaryport.model.AgregarAsesorFichaCommand;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;

public final class AgregarAsesorFichaMapper {

    private AgregarAsesorFichaMapper() {}

    public static AsesorFichaDomain toDomain(AgregarAsesorFichaCommand command) {
        return AsesorFichaDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
