package com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.mapper;

import com.arquisoft.artefactos.application.estudianteproyectogrado.command.result.DestitucionEstudianteProyectoGradoResult;
import com.arquisoft.artefactos.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;

import java.time.Instant;

public final class DestitucionEstudianteProyectoGradoResultMapper {

    private DestitucionEstudianteProyectoGradoResultMapper() {}

    public static DestitucionEstudianteProyectoGradoResult.Destituida toResultDestituida(
            EstudianteProyectoGradoDomain vinculo) {
        return new DestitucionEstudianteProyectoGradoResult.Destituida(
                vinculo.getEstudiante(), vinculo.getProyectoGrado());
    }

    public static DestitucionEstudianteProyectoGradoResult.Lapida toResultLapida(
            EstudianteProyectoGradoDomain vinculo) {
        return new DestitucionEstudianteProyectoGradoResult.Lapida(
                vinculo.getEstudiante(), vinculo.getProyectoGrado());
    }

    public static DestitucionEstudianteProyectoGradoResult.Descartada toResultDescartada(
            EstudianteProyectoGradoDomain vinculo, Instant ocurridoEnVigente) {
        return new DestitucionEstudianteProyectoGradoResult.Descartada(
                vinculo.getEstudiante(), vinculo.getProyectoGrado(), ocurridoEnVigente);
    }
}
