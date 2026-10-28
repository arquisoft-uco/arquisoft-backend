package com.arquisoft.usuarios.application.coordinador.command.validator;

import com.arquisoft.usuarios.domain.coordinador.CoordinadorDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public interface AgregarCoordinadorValidator {

    void validar(UsuarioDomain usuario, CoordinadorDomain coordinador);
}
