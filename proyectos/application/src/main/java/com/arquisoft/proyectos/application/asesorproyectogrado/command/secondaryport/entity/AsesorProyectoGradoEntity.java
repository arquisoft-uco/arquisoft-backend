package com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record AsesorProyectoGradoEntity(UUID id, UUID asesor, UUID proyectoGrado, String estadoAsesor,
                                        Instant fechaAsignado) {
}
