package com.arquisoft.proyectos.application.asesorproyectogrado.query.validator;

import java.util.UUID;

public interface ConsultarAsesoresProyectoGradoCoordinadorValidator {

    void validar(UUID proyectoGrado, UUID coordinadorEsperado, UUID coordinadorSolicitante);
}
