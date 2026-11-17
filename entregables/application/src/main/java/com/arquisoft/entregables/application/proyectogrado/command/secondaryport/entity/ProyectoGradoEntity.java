package com.arquisoft.entregables.application.proyectogrado.command.secondaryport.entity;

import java.time.Instant;
import java.util.UUID;

public record ProyectoGradoEntity(UUID id, String nombre, String estadoProyecto, Instant ocurridoEn) {
}
