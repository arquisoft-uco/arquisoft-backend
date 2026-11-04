package com.arquisoft.proyectos.application.estudianteproyectogrado.query.readmodel;

import java.util.UUID;

public record EstudianteAsesoradoProyectoGradoReadModel(
        UUID estudianteId, String identificador, String nombre, String email) {}
