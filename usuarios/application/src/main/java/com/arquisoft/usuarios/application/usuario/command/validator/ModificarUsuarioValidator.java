package com.arquisoft.usuarios.application.usuario.command.validator;

import com.arquisoft.usuarios.domain.usuario.ModificacionUsuarioDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public interface ModificarUsuarioValidator {

    void validar(ModificacionUsuarioDomain modificacion, UsuarioDomain encontrado,
                 boolean identificadorDuplicado, boolean emailDuplicado, boolean contactoDuplicado);
}
