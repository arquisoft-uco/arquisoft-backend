package com.arquisoft.entregables.application.proyectogrado.command.result;

import java.util.UUID;

public sealed interface AgregacionProyectoGradoResult {

    record Agregado(UUID proyectoGrado) implements AgregacionProyectoGradoResult {}

    record Duplicado(UUID proyectoGrado) implements AgregacionProyectoGradoResult {}
}
