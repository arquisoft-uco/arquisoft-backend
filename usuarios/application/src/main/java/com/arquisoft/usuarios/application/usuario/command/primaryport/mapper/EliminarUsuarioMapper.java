package com.arquisoft.usuarios.application.usuario.command.primaryport.mapper;

import com.arquisoft.usuarios.application.usuario.command.primaryport.model.EliminarUsuarioCommand;
import com.arquisoft.usuarios.domain.usuario.EliminacionUsuarioDomain;

public final class EliminarUsuarioMapper {

    private EliminarUsuarioMapper() {}

    public static EliminacionUsuarioDomain toDomain(EliminarUsuarioCommand command) {
        return EliminacionUsuarioDomain.crear(command.usuario());
    }
}
