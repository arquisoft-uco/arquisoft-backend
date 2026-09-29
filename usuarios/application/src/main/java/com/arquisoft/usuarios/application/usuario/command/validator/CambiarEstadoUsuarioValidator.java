package com.arquisoft.usuarios.application.usuario.command.validator;

import com.arquisoft.usuarios.domain.usuario.CambioEstadoUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public interface CambiarEstadoUsuarioValidator {

    void validar(CambioEstadoUsuarioDomain cambio, UsuarioDomain encontrado);
}
