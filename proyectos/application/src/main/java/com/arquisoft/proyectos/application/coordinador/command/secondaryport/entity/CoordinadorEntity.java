package com.arquisoft.proyectos.application.coordinador.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record CoordinadorEntity(UUID id, String identificador, String nombre, String email, Instant ocurridoEn) {
}
