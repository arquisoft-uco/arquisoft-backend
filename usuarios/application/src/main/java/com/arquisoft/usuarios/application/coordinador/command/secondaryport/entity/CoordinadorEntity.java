package com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record CoordinadorEntity(UUID usuario, Instant eliminadoEn) {
}
