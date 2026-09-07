package com.arquisoft.evaluaciones.application.proyectoestudianteacceso.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record ProyectoEstudianteAccesoEntity(
        UUID proyecto,
        UUID estudiante,
        boolean activo,
        Instant ocurridoEn
) {
}
