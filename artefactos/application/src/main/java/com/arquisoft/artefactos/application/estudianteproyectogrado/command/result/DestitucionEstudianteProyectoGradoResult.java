package com.arquisoft.artefactos.application.estudianteproyectogrado.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface DestitucionEstudianteProyectoGradoResult {

    record Destituida(UUID estudiante, UUID proyectoGrado) implements DestitucionEstudianteProyectoGradoResult {}

    record Lapida(UUID estudiante, UUID proyectoGrado) implements DestitucionEstudianteProyectoGradoResult {}

    record Descartada(UUID estudiante, UUID proyectoGrado, Instant ocurridoEnVigente)
            implements DestitucionEstudianteProyectoGradoResult {}
}
