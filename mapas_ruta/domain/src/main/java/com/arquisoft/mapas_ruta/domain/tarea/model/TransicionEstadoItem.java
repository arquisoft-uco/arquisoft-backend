package com.arquisoft.mapas_ruta.domain.tarea.model;

import java.util.UUID;

public record TransicionEstadoItem(UUID tarea, EstadoItem actual, EstadoItem nuevo) {}
