package com.arquisoft.mapas_ruta.application.planreal.command.result;

import java.util.UUID;

public sealed interface GeneracionPlanRealResult {

    record Generada(UUID tarea, int tiempoRealDias) implements GeneracionPlanRealResult {}

    record Recalculada(UUID tarea, int tiempoRealDias) implements GeneracionPlanRealResult {}

    record Descartada(UUID tarea) implements GeneracionPlanRealResult {}
}
