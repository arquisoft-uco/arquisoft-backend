package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result;

import java.time.Instant;
import java.util.UUID;

public sealed interface RemocionEstudianteProyectoGradoResult {

    record Removida(UUID proyectoGrado, UUID autor) implements RemocionEstudianteProyectoGradoResult {}

    record Lapida(UUID proyectoGrado, UUID autor) implements RemocionEstudianteProyectoGradoResult {}

    record Descartada(UUID proyectoGrado, UUID autor, Instant ocurridoEnVigente)
            implements RemocionEstudianteProyectoGradoResult {}
}
