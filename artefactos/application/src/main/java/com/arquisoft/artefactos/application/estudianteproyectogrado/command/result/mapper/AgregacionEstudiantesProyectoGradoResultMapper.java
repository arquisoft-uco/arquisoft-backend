package com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.mapper;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.AgregacionEstudiantesProyectoGradoResult;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.AgregacionEstudiantesProyectoGradoDomain;

public final class AgregacionEstudiantesProyectoGradoResultMapper {

    private AgregacionEstudiantesProyectoGradoResultMapper() {}

    public static AgregacionEstudiantesProyectoGradoResult toResult(
            AgregacionEstudiantesProyectoGradoDomain agregacion,
            int agregados, int reactivados, int duplicados, int descartados) {
        return new AgregacionEstudiantesProyectoGradoResult(
                agregacion.getProyectoGrado(), agregados, reactivados, duplicados, descartados);
    }
}
