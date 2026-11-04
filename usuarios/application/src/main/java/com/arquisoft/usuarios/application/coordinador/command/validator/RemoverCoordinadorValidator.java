package com.arquisoft.usuarios.application.coordinador.command.validator;

import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface RemoverCoordinadorValidator {

    void validar(UUID usuario, UsuarioDomain usuarioDomain, CoordinadorDomain coordinador);
}
