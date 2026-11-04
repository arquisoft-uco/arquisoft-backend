package com.arquisoft.entregables.application.estudianteproyectogrado.command.result.mapper;

import com.arquisoft.entregables.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.entregables.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;

import java.time.Instant;

public final class RemocionEstudianteProyectoGradoResultMapper {

    private RemocionEstudianteProyectoGradoResultMapper() {}

    public static RemocionEstudianteProyectoGradoResult.Removida toResultRemovida(
            EstudianteProyectoGradoDomain vinculo) {
        return new RemocionEstudianteProyectoGradoResult.Removida(
                vinculo.getProyectoGrado(), vinculo.getEstudiante());
    }

    public static RemocionEstudianteProyectoGradoResult.Lapida toResultLapida(
            EstudianteProyectoGradoDomain vinculo) {
        return new RemocionEstudianteProyectoGradoResult.Lapida(
                vinculo.getProyectoGrado(), vinculo.getEstudiante());
    }

    public static RemocionEstudianteProyectoGradoResult.Descartada toResultDescartada(
            EstudianteProyectoGradoDomain vinculo, Instant ocurridoEnVigente) {
        return new RemocionEstudianteProyectoGradoResult.Descartada(
                vinculo.getProyectoGrado(), vinculo.getEstudiante(), ocurridoEnVigente);
    }
}
