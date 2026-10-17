package com.arquisoft.evaluaciones.application.jurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.jurado.command.primaryport.model.AgregarJuradoCommand;
import com.arquisoft.evaluaciones.domain.jurado.JuradoDomain;

public final class AgregarJuradoMapper {

    private AgregarJuradoMapper() {}

    public static JuradoDomain toDomain(AgregarJuradoCommand command) {
        return JuradoDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
