package com.arquisoft.usuarios.application.coordinador.command.validator;

import java.util.UUID;

public interface AgregarCoordinadorValidator {

    void validar(UUID usuario, boolean yaEsCoordinador);
}
