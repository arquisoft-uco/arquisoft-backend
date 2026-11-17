package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.mapper;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;

import java.util.UUID;

public final class AgregacionEstudiantesProyectoGradoResultMapper {

    private AgregacionEstudiantesProyectoGradoResultMapper() {}

    public static AgregacionEstudiantesProyectoGradoResult toResult(
            UUID proyectoGrado, int agregados, int reactivados, int duplicados, int descartados) {
        return new AgregacionEstudiantesProyectoGradoResult(
                proyectoGrado, agregados, reactivados, duplicados, descartados);
    }
}
