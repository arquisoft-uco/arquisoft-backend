package com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record EntregableProyectoGradoEntity(UUID id, UUID proyectoGrado, int version, String estadoEntregable,
                                            Instant ocurridoEn) {
}
