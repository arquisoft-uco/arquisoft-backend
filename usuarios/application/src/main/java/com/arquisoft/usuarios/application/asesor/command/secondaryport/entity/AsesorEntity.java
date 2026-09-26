package com.arquisoft.usuarios.application.asesor.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record AsesorEntity(UUID usuario, Instant eliminadoEn) {
}
