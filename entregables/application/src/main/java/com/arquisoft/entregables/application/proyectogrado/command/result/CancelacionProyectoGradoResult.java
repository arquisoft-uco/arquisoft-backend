package com.arquisoft.entregables.application.proyectogrado.command.result;

import java.util.UUID;

public sealed interface CancelacionProyectoGradoResult {

    record Cancelado(UUID proyectoGrado) implements CancelacionProyectoGradoResult {}

    record Duplicado(UUID proyectoGrado) implements CancelacionProyectoGradoResult {}
}
