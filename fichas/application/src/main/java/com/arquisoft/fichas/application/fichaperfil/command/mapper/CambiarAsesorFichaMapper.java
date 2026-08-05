package com.arquisoft.fichas.application.fichaperfil.command.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.FichaPerfilDomain;

public final class CambiarAsesorFichaMapper {

    private CambiarAsesorFichaMapper() {}

    public static FichaPerfilDomain toDomain(CambiarAsesorFichaCommand command) {
        return FichaPerfilDomain.crear(command.fichaPerfil(), command.nuevoAsesorFicha());
    }
}
