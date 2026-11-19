package com.arquisoft.proyectos.application.proyectogrado.command.result;

import java.util.UUID;

public sealed interface ReanudacionProyectoGradoResult {

    record Reanudado(UUID proyectoGrado) implements ReanudacionProyectoGradoResult {}

    record Omitido(UUID proyectoGrado, String estadoActual, boolean plazoVigente)
            implements ReanudacionProyectoGradoResult {}
}
