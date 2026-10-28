package com.arquisoft.mapas_ruta.application.planestimado.command.secondaryport.entity;

import java.util.UUID;

public record PlanEstimadoEntity(UUID id, UUID tarea, int tiempoEstimadoDias, String descripcionEstimado) {}
