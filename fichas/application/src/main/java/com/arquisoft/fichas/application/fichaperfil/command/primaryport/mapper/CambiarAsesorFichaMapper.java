package com.arquisoft.fichas.application.fichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.CambiarAsesorFichaCommand;
import com.arquisoft.fichas.domain.fichaperfil.CambioAsesorFichaDomain;

public final class CambiarAsesorFichaMapper {

    private CambiarAsesorFichaMapper() {}

    public static CambioAsesorFichaDomain toDomain(CambiarAsesorFichaCommand command) {
        return CambioAsesorFichaDomain.crear(command.fichaPerfil(), command.nuevoAsesorFicha());
    }
}
