package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.result;

import java.util.UUID;

public sealed interface AgregacionCoordinadorProyectoGradoResult {

    record Agregada(UUID proyectoGrado) implements AgregacionCoordinadorProyectoGradoResult {}

    record Duplicada(UUID proyectoGrado) implements AgregacionCoordinadorProyectoGradoResult {}
}
