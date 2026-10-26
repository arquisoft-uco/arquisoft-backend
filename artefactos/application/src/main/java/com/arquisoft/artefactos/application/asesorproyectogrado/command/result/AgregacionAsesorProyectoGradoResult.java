package com.arquisoft.artefactos.application.asesorproyectogrado.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface AgregacionAsesorProyectoGradoResult {

    record Agregada(UUID asesor, UUID proyectoGrado) implements AgregacionAsesorProyectoGradoResult {}

    record Reactivada(UUID asesor, UUID proyectoGrado) implements AgregacionAsesorProyectoGradoResult {}

    record Duplicada(UUID asesor, UUID proyectoGrado) implements AgregacionAsesorProyectoGradoResult {}

    record Descartada(UUID asesor, UUID proyectoGrado, Instant ocurridoEnVigente)
            implements AgregacionAsesorProyectoGradoResult {}
}
