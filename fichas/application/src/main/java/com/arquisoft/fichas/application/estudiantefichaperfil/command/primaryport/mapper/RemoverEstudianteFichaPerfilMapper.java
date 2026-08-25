package com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.mapper;

import com.arquisoft.fichas.application.estudiantefichaperfil.command.primaryport.model.RemoverEstudianteFichaPerfilCommand;
import com.arquisoft.fichas.domain.estudiantefichaperfil.RemocionEstudianteFichaPerfilDomain;

public final class RemoverEstudianteFichaPerfilMapper {

    private RemoverEstudianteFichaPerfilMapper() {}

    public static RemocionEstudianteFichaPerfilDomain toDomain(RemoverEstudianteFichaPerfilCommand command) {
        return RemocionEstudianteFichaPerfilDomain.crear(command.fichaPerfil(), command.estudiante());
    }
}
