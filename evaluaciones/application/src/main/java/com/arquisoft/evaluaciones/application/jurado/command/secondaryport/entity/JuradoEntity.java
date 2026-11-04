package com.arquisoft.evaluaciones.application.jurado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record JuradoEntity(UUID id, String identificador, String nombre, String email, Instant ocurridoEn,
                           Instant eliminadoEn) {
}
