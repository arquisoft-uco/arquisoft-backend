package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface RemocionEstudianteProyectoGradoResult {

    record Removida(UUID proyectoGrado, UUID estudiante) implements RemocionEstudianteProyectoGradoResult {}

    record Lapida(UUID proyectoGrado, UUID estudiante) implements RemocionEstudianteProyectoGradoResult {}

    record Descartada(UUID proyectoGrado, UUID estudiante, Instant ocurridoEnVigente)
            implements RemocionEstudianteProyectoGradoResult {}
}
