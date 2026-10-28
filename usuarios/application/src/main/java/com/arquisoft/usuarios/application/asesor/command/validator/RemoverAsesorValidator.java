package com.arquisoft.usuarios.application.asesor.command.validator;

import com.arquisoft.usuarios.domain.asesor.AsesorDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface RemoverAsesorValidator {

    void validar(UUID usuario, UsuarioDomain usuarioDomain, AsesorDomain asesor);
}
