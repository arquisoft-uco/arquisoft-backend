package com.arquisoft.fichas.application.fichaperfil.command.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
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
