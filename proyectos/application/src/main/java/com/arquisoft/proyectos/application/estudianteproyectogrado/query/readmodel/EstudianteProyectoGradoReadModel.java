package com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel;

import java.util.UUID;

public record EstudianteProyectoGradoReadModel(
        UUID id,
        UUID proyectoGradoId,
        UUID estudianteId,
        String identificador,
        String nombre,
        String email,
        boolean vigente
) {}
