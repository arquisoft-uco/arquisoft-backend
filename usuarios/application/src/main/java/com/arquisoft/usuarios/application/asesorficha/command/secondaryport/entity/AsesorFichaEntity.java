package com.arquisoft.usuarios.application.asesorficha.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record AsesorFichaEntity(UUID usuario, Instant eliminadoEn) {
}
