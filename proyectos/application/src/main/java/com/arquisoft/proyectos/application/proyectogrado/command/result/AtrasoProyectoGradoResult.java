package com.arquisoft.proyectos.application.proyectogrado.command.result;

import java.util.UUID;

public sealed interface AtrasoProyectoGradoResult {

    record Atrasado(UUID proyectoGrado) implements AtrasoProyectoGradoResult {}

    record Omitido(UUID proyectoGrado, String estadoActual) implements AtrasoProyectoGradoResult {}
}
