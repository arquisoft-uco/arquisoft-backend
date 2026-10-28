package com.arquisoft.mapas_ruta.domain.planestimado.model;

import java.util.UUID;

public record DisponibilidadPlanEstimado(UUID tarea, String descripcionTarea, boolean existe) {}
