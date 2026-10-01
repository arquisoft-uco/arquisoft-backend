package com.arquisoft.usuarios.application.usuario.command.validator;

import com.arquisoft.usuarios.domain.usuario.RegistroUsuarioDomain;

public interface RegistrarUsuarioValidator {

    void validar(RegistroUsuarioDomain registro, boolean identificadorYaExiste, boolean emailYaExiste,
                 boolean contactoYaExiste);
}
