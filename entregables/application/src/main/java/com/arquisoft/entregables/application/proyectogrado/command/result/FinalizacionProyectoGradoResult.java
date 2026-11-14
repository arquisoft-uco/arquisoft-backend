package com.arquisoft.entregables.application.proyectogrado.command.result;

import java.util.UUID;

public sealed interface FinalizacionProyectoGradoResult {

    record Finalizado(UUID proyectoGrado) implements FinalizacionProyectoGradoResult {}

    record Duplicado(UUID proyectoGrado) implements FinalizacionProyectoGradoResult {}
}
