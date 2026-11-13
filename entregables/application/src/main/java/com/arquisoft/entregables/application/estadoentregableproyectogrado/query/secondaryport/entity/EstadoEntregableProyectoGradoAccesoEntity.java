package com.arquisoft.entregables.application.estadoentregableproyectogrado.query.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record EstadoEntregableProyectoGradoAccesoEntity(UUID id, UUID entregableProyectoGrado,
                                                        String estadoEntregable, Instant fechaActualizacion) {
}
