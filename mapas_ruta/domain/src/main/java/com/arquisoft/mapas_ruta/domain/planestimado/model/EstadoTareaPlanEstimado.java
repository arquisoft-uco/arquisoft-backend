package com.arquisoft.mapas_ruta.domain.planestimado.model;

import com.arquisoft.mapas_ruta.domain.tarea.model.EstadoItem;

import java.util.UUID;

public record EstadoTareaPlanEstimado(UUID tarea, String descripcionTarea, EstadoItem estadoItem) {}
