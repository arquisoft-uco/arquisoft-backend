package com.arquisoft.evaluaciones.application.jurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.jurado.command.primaryport.model.RemoverJuradoCommand;
import com.arquisoft.evaluaciones.domain.jurado.JuradoDomain;

public final class RemoverJuradoMapper {

    private RemoverJuradoMapper() {}

    public static JuradoDomain toDomain(RemoverJuradoCommand command) {
        return JuradoDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
