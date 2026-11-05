package com.arquisoft.usuarios.application.usuario.query.validator;

import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public interface ConsultarUsuarioPorCorreoValidator {

    void validar(UsuarioDomain encontrado);
}
