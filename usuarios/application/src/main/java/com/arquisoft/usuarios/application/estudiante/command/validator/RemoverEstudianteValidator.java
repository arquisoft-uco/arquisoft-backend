package com.arquisoft.usuarios.application.estudiante.command.validator;

import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;

import java.util.UUID;

public interface RemoverEstudianteValidator {

    void validar(UUID usuario, EstudianteDomain estudiante);
}
