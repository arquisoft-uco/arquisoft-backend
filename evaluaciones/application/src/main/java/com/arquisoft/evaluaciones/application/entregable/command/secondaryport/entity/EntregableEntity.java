package com.arquisoft.evaluaciones.application.entregable.command.secondaryport.entity;

import java.util.UUID;

public record EntregableEntity(UUID id, UUID proyectoGrado, String proyecto, Integer version) {
}
