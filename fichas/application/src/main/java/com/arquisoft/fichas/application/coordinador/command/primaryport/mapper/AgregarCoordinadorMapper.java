package com.arquisoft.fichas.application.coordinador.command.primaryport.mapper;

import com.arquisoft.fichas.application.coordinador.command.primaryport.model.AgregarCoordinadorCommand;
import com.arquisoft.fichas.domain.coordinador.CoordinadorDomain;

public final class AgregarCoordinadorMapper {

    private AgregarCoordinadorMapper() {}

    public static CoordinadorDomain toDomain(AgregarCoordinadorCommand command) {
        return CoordinadorDomain.crear(
                command.id(),
                command.identificador(),
                command.nombre(),
                command.email(),
                command.ocurridoEn());
    }
}
