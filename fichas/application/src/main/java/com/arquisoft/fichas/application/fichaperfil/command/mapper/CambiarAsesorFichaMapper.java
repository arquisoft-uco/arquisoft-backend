package com.arquisoft.fichas.application.fichaperfil.command.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;

public final class CambiarAsesorFichaMapper {

    private CambiarAsesorFichaMapper() {}

    public static CambioAsesorFichaDomain toDomain(CambiarAsesorFichaCommand command) {
        return CambioAsesorFichaDomain.crear(command.fichaPerfil(), command.nuevoAsesorFicha());
    }
}
