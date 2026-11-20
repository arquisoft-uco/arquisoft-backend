package com.arquisoft.entregables.application.estudianteproyectogrado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record EstudianteProyectoGradoEntity(UUID id, UUID estudiante, UUID proyectoGrado, String nombre, String email,
                                            Instant ocurridoEn, Instant eliminadoEn) {
}
