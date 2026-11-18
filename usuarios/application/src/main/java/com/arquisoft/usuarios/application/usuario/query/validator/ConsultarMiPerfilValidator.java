package com.arquisoft.usuarios.application.usuario.query.validator;

import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface ConsultarMiPerfilValidator {

    void validar(UUID usuario, UsuarioDomain encontrado);
}
