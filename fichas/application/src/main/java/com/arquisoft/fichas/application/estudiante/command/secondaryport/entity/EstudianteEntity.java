package com.arquisoft.fichas.application.estudiante.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record EstudianteEntity(UUID id, String identificador, String nombre, String email, Instant ocurridoEn,
                               Instant eliminadoEn) {
}
