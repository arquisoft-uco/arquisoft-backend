package com.arquisoft.usuarios.application.asesor.command.validator;

import com.arquisoft.usuarios.domain.asesor.AsesorDomain;

import java.util.UUID;

public interface RemoverAsesorValidator {

    void validar(UUID usuario, AsesorDomain asesor);
}
