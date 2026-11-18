package com.arquisoft.artefactos.application.proyectogrado.command.result;

import java.util.UUID;

public sealed interface CongelacionProyectoGradoResult {

    record Congelado(UUID proyectoGrado) implements CongelacionProyectoGradoResult {}

    record YaCongelado(UUID proyectoGrado) implements CongelacionProyectoGradoResult {}
}
