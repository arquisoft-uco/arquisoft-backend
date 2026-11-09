package com.arquisoft.artefactos.application.proyectogrado.command.result;

import java.util.UUID;

public sealed interface DescongelacionProyectoGradoResult {

    record Descongelado(UUID proyectoGrado) implements DescongelacionProyectoGradoResult {}

    record Omitido(UUID proyectoGrado) implements DescongelacionProyectoGradoResult {}
}
