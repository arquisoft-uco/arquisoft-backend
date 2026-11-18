package com.arquisoft.proyectos.application.asesorproyectogrado.query.readmodel;

import java.util.UUID;

public record AsesorActualProyectoGradoReadModel(
        UUID proyectoGradoId,
        String tituloProyecto,
        UUID asesorId,
        String identificador,
        String nombre,
        String email
) {}
