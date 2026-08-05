package com.arquisoft.fichas.application.fichaperfil.command.mapper;

import com.arquisoft.fichas.application.fichaperfil.command.model.ModificarFichaPerfilCommand;
import com.arquisoft.fichas.domain.fichaperfil.aggregate.ModificarFichaPerfilDomain;

public final class ModificarFichaPerfilMapper {

    private ModificarFichaPerfilMapper() {}

    public static ModificarFichaPerfilDomain toDomain(ModificarFichaPerfilCommand command) {
        return ModificarFichaPerfilDomain.crear(
                command.fichaPerfil(),
                command.tituloProyecto(),
                command.estudiante());
    }
}
