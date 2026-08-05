package com.arquisoft.fichas.application.fichaperfil.command.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.CambiarAsesorFichaDomain;

public final class CambiarAsesorFichaMapper {

    private CambiarAsesorFichaMapper() {}

    public static CambiarAsesorFichaDomain toDomain(CambiarAsesorFichaCommand command) {
        return CambiarAsesorFichaDomain.crear(command.fichaPerfil(), command.nuevoAsesorFicha());
    }
}
