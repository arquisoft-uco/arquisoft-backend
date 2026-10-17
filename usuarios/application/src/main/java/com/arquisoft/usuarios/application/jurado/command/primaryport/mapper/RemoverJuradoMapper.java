package com.arquisoft.usuarios.application.jurado.command.primaryport.mapper;

import com.arquisoft.usuarios.application.jurado.command.primaryport.model.RemoverJuradoCommand;
import com.arquisoft.usuarios.domain.jurado.JuradoDomain;

public final class RemoverJuradoMapper {

    private RemoverJuradoMapper() {}

    public static JuradoDomain toDomain(RemoverJuradoCommand command) {
        return JuradoDomain.crear(command.usuario());
    }
}
