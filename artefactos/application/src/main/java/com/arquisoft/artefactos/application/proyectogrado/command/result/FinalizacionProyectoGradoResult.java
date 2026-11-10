package com.arquisoft.artefactos.application.proyectogrado.command.result;

import java.util.UUID;

public sealed interface FinalizacionProyectoGradoResult {

    record Finalizado(UUID proyectoGrado) implements FinalizacionProyectoGradoResult {}

    record YaFinalizado(UUID proyectoGrado) implements FinalizacionProyectoGradoResult {}
}
