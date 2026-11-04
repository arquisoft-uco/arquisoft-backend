package com.arquisoft.proyectos.application.estadoproyectogrado.command.validator;

import java.util.UUID;

public interface AsignarEstadoInicialProyectoGradoValidator {

    void validar(UUID proyectoGrado, boolean proyectoExiste);
}
