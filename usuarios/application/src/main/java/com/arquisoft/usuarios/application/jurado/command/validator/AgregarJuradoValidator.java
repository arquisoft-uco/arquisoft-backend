package com.arquisoft.usuarios.application.jurado.command.validator;

import com.arquisoft.usuarios.domain.jurado.JuradoDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public interface AgregarJuradoValidator {

    void validar(UsuarioDomain usuario, JuradoDomain jurado);
}
