package com.arquisoft.usuarios.application.estudiante.command.validator;

import java.util.UUID;

public interface AgregarEstudianteValidator {

    void validar(UUID usuario, boolean yaEsEstudiante);
}
