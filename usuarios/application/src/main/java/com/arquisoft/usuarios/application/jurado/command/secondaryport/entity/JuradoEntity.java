package com.arquisoft.usuarios.application.jurado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record JuradoEntity(UUID usuario, Instant eliminadoEn) {
}
