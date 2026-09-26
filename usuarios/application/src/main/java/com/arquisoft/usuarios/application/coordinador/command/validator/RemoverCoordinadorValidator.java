package com.arquisoft.usuarios.application.coordinador.command.validator;

import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;

import java.util.UUID;

public interface RemoverCoordinadorValidator {

    void validar(UUID usuario, CoordinadorDomain coordinador);
}
