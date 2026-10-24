package com.arquisoft.artefactos.application.estudianteartefacto.query.readmodel;

import java.util.UUID;

public record EstudianteArtefactoReadModel(
        UUID id,
        UUID artefactoId,
        UUID estudianteId,
        String identificador,
        String nombre,
        String email,
        boolean vigente
) {
}
