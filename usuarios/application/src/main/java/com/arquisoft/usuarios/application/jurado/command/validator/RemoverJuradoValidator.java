package com.arquisoft.usuarios.application.jurado.command.validator;

import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface RemoverJuradoValidator {

    void validar(UUID usuario, UsuarioDomain usuarioDomain, JuradoDomain jurado);
}
