package com.arquisoft.fichas.application.fichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.RegistrarFichaPerfilCommand;
import com.arquisoft.fichas.domain.fichaperfil.FichaPerfilDomain;

public final class RegistrarFichaPerfilMapper {

    private RegistrarFichaPerfilMapper() {}

    public static FichaPerfilDomain toDomain(RegistrarFichaPerfilCommand command) {
        return FichaPerfilDomain.crear(command.tituloProyecto(), command.asesorFicha());
    }
}
