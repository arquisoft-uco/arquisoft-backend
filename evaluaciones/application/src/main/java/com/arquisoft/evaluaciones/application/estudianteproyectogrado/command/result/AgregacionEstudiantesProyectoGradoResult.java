package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result;

import java.util.UUID;

public record AgregacionEstudiantesProyectoGradoResult(
        UUID proyectoGrado, int agregados, int reactivados, int duplicados, int descartados) {
}
