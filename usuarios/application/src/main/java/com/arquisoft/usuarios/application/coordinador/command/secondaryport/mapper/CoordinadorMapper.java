package com.arquisoft.usuarios.application.coordinador.command.secondaryport.mapper;

import com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;

public final class CoordinadorMapper {

    private CoordinadorMapper() {}

    public static CoordinadorEntity toEntity(CoordinadorDomain coordinador) {
        return new CoordinadorEntity(coordinador.getUsuario());
    }
}
