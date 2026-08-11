package com.arquisoft.usuarios.application.usuario.command.validator;

import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public interface CrearUsuarioValidator {

    void validar(UsuarioDomain usuario);
}
