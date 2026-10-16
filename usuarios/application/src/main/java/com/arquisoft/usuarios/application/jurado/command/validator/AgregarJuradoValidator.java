package com.arquisoft.usuarios.application.jurado.command.validator;

import com.arquisoft.usuarios.domain.jurado.JuradoDomain;

import java.util.UUID;

public interface AgregarJuradoValidator {

    void validar(UUID usuario, JuradoDomain jurado);
}
