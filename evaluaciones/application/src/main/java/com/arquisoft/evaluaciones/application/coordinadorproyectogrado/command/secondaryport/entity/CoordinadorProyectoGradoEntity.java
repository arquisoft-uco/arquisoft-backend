package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record CoordinadorProyectoGradoEntity(
        UUID proyectoGrado, UUID coordinador, String nombre, String email, Instant ocurridoEn) {
}
