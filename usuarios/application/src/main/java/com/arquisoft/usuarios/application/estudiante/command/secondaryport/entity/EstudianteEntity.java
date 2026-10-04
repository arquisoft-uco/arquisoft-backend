package com.arquisoft.usuarios.application.estudiante.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record EstudianteEntity(UUID usuario, Instant eliminadoEn) {
}
