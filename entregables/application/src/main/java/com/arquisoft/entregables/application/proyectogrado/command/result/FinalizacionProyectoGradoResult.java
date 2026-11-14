package com.arquisoft.entregables.application.proyectogrado.command.result;

import java.util.UUID;

public sealed interface FinalizacionProyectoGradoResult {

    record Finalizado(UUID proyectoGrado, int artefactosEliminados) implements FinalizacionProyectoGradoResult {}

    record Duplicado(UUID proyectoGrado, int artefactosEliminados) implements FinalizacionProyectoGradoResult {}
}
