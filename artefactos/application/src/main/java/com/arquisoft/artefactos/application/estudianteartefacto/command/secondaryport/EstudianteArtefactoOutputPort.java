package com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport;

import java.util.UUID;

public interface EstudianteArtefactoOutputPort {

    boolean existePorArtefactoYEstudiante(UUID artefacto, UUID estudiante);
}
