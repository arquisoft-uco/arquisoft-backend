package com.arquisoft.usuarios.application.administrador.command.validator;

import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;

import java.util.UUID;

public interface AgregarAdministradorValidator {

    void validar(UUID usuario, AdministradorDomain administrador);
}
