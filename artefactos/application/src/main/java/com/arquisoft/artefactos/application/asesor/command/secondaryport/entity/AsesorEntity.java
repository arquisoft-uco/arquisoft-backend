package com.arquisoft.artefactos.application.asesor.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record AsesorEntity(UUID id, String identificador, String nombre, String email, Instant ocurridoEn,
                           Instant eliminadoEn) {
}
