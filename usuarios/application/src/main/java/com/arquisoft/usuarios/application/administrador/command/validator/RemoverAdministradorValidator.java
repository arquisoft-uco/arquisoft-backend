package com.arquisoft.usuarios.application.administrador.command.validator;

import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;

import java.util.UUID;

public interface RemoverAdministradorValidator {

    void validar(UUID actor, UUID usuario, AdministradorDomain administrador, long administradoresVigentes);
}
