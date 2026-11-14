package com.arquisoft.entregables.application.proyectogrado.command.result;

import java.util.UUID;

public sealed interface CancelacionProyectoGradoResult {

    record Cancelado(UUID proyectoGrado, int paquetesEliminados, int artefactosEliminados)
            implements CancelacionProyectoGradoResult {}

    record Duplicado(UUID proyectoGrado, int paquetesEliminados, int artefactosEliminados)
            implements CancelacionProyectoGradoResult {}
}
