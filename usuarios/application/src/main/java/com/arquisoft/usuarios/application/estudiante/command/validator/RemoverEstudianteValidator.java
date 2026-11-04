package com.arquisoft.usuarios.application.estudiante.command.validator;

import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface RemoverEstudianteValidator {

    void validar(UUID usuario, UsuarioDomain usuarioDomain, EstudianteDomain estudiante);
}
