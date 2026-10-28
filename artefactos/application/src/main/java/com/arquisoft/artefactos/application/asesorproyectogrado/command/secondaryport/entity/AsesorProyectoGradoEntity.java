package com.arquisoft.artefactos.application.asesorproyectogrado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record AsesorProyectoGradoEntity(UUID id, UUID asesor, UUID proyectoGrado, Instant ocurridoEn,
                                        Instant finalizadoEn) {
}
