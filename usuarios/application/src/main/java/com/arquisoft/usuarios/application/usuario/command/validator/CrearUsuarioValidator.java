package com.arquisoft.usuarios.application.usuario.command.validator;

import com.arquisoft.usuarios.domain.usuario.aggregate.UsuarioAggregate;

public interface CrearUsuarioValidator {

    void validar(UsuarioAggregate usuario);
}
