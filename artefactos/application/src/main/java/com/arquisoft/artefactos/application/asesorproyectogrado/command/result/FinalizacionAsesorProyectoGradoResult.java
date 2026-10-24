package com.arquisoft.artefactos.application.asesorproyectogrado.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface FinalizacionAsesorProyectoGradoResult {

    record Finalizada(UUID asesor, UUID proyectoGrado) implements FinalizacionAsesorProyectoGradoResult {}

    record Lapida(UUID asesor, UUID proyectoGrado) implements FinalizacionAsesorProyectoGradoResult {}

    record Descartada(UUID asesor, UUID proyectoGrado, Instant ocurridoEnVigente)
            implements FinalizacionAsesorProyectoGradoResult {}
}
