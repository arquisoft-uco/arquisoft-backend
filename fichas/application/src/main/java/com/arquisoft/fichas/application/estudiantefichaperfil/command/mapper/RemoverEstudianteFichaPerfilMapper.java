package com.arquisoft.fichas.application.estudiantefichaperfil.command.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.model.RemoverEstudianteFichaPerfilCommand;
import com.arquisoft.fichas.domain.estudiantefichaperfil.RemoverEstudianteFichaPerfilDomain;

public final class RemoverEstudianteFichaPerfilMapper {

    private RemoverEstudianteFichaPerfilMapper() {}

    public static RemoverEstudianteFichaPerfilDomain toDomain(RemoverEstudianteFichaPerfilCommand command) {
        return RemoverEstudianteFichaPerfilDomain.crear(command.fichaPerfil(), command.estudiante());
    }
}
