package com.arquisoft.usuarios.application.estudiante.command.validator;

import com.arquisoft.usuarios.domain.estudiante.EstudianteDomain;
import com.arquisoft.usuarios.domain.usuario.UsuarioDomain;

public interface AgregarEstudianteValidator {

    void validar(UsuarioDomain usuario, EstudianteDomain estudiante);
}
