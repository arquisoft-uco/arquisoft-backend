package com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record EstadoEntregableProyectoGradoEntity(UUID id, UUID entregableProyectoGrado, String estadoEntregable,
                                                  Instant fechaActualizacion) {
}
