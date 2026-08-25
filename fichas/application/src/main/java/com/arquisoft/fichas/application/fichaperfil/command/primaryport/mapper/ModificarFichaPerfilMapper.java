package com.arquisoft.fichas.application.fichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.primaryport.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.domain.fichaperfil.ModificacionFichaPerfilDomain;

public final class ModificarFichaPerfilMapper {

    private ModificarFichaPerfilMapper() {}

    public static ModificacionFichaPerfilDomain toDomain(ModificarFichaPerfilCommand command) {
        return ModificacionFichaPerfilDomain.crear(
                command.fichaPerfil(),
                command.tituloProyecto(),
                command.estudiante());
    }
}
