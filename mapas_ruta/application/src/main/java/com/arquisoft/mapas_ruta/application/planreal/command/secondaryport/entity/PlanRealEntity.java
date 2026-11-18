package com.arquisoft.mapas_ruta.application.planreal.command.secondaryport.entity;

import java.util.UUID;

public record PlanRealEntity(UUID id, UUID tarea, int tiempoRealDias, String descripcionReal) {}
