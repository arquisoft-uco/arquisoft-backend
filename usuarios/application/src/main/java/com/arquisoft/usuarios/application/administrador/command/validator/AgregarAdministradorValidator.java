package com.arquisoft.usuarios.application.administrador.command.validator;

import com.arquisoft.usuarios.domain.administrador.AdministradorDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public interface AgregarAdministradorValidator {

    void validar(UsuarioDomain usuario, AdministradorDomain administrador);
}
