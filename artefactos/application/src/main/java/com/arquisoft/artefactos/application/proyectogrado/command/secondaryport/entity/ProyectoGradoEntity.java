package com.arquisoft.artefactos.application.proyectogrado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record ProyectoGradoEntity(UUID id, String nombre, Instant entregadoEn, Instant finalizadoEn,
                                 Instant canceladoEn) {
}
