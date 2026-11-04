package com.arquisoft.usuarios.application.administrador.command.validator;

import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface RemoverAdministradorValidator {

    void validar(UUID actor, UUID usuario, UsuarioDomain usuarioDomain, AdministradorDomain administrador,
                 long administradoresVigentes);
}
