package com.arquisoft.artefactos.application.estudianteproyectogrado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record EstudianteProyectoGradoEntity(UUID id, UUID estudiante, UUID proyectoGrado, Instant ocurridoEn,
                                            Instant destituidoEn) {
}
