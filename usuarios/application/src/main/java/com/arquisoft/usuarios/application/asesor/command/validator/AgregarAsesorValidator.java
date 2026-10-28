package com.arquisoft.usuarios.application.asesor.command.validator;

import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public interface AgregarAsesorValidator {

    void validar(UsuarioDomain usuario, AsesorDomain asesor);
}
