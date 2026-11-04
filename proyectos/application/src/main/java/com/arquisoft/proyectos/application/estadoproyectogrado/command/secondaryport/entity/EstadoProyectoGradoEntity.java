package com.arquisoft.proyectos.application.estadoproyectogrado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record EstadoProyectoGradoEntity(UUID id, UUID proyectoGrado, String estadoProyecto,
                                        Instant fechaActualizacion) {
}
