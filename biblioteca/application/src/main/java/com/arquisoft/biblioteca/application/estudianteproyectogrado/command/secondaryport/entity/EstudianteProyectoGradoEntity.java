package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record EstudianteProyectoGradoEntity(UUID id, UUID autor, UUID proyectoGrado, Instant ocurridoEn,
                                            Instant eliminadoEn) {
}
