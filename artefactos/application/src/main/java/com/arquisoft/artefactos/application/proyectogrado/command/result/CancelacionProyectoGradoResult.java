package com.arquisoft.artefactos.application.proyectogrado.command.result;

import java.util.UUID;

public sealed interface CancelacionProyectoGradoResult {

    record Cancelado(UUID proyectoGrado) implements CancelacionProyectoGradoResult {}

    record YaCancelado(UUID proyectoGrado) implements CancelacionProyectoGradoResult {}
}
