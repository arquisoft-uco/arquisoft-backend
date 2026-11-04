package com.arquisoft.proyectos.application.estudianteproyectogrado.query.validator;

import java.util.UUID;

public interface ConsultarEstudiantesProyectoGradoCoordinadorValidator {

    void validar(UUID proyectoGrado, UUID coordinadorEsperado, UUID coordinadorSolicitante);
}
