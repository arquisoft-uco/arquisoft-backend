package com.arquisoft.usuarios.application.usuario.command.validator;

import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.model.RolesUsuario;

import java.util.UUID;

public interface EliminarUsuarioValidator {

    void validar(UUID usuario, UsuarioDomain encontrado, RolesUsuario roles);
}
