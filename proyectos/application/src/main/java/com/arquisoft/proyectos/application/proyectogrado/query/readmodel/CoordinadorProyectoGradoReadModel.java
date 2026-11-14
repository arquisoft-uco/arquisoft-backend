package com.arquisoft.proyectos.application.proyectogrado.query.readmodel;

import java.util.UUID;

public record CoordinadorProyectoGradoReadModel(
        UUID proyectoGradoId,
        String tituloProyecto,
        UUID coordinadorId,
        String identificador,
        String nombre,
        String email
) {}
