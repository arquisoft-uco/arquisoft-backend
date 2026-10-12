package com.arquisoft.usuarios.application.representantecomite.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record RepresentanteComiteEntity(UUID usuario, Instant eliminadoEn) {
}
