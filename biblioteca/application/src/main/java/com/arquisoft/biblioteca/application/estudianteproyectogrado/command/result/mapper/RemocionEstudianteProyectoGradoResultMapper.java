package com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.mapper;

import com.arquisoft.biblioteca.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.biblioteca.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;

import java.time.Instant;

public final class RemocionEstudianteProyectoGradoResultMapper {

    private RemocionEstudianteProyectoGradoResultMapper() {}

    public static RemocionEstudianteProyectoGradoResult.Removida toResultRemovida(
            EstudianteProyectoGradoDomain vinculo) {
        return new RemocionEstudianteProyectoGradoResult.Removida(vinculo.getProyectoGrado(), vinculo.getAutor());
    }

    public static RemocionEstudianteProyectoGradoResult.Lapida toResultLapida(
            EstudianteProyectoGradoDomain vinculo) {
        return new RemocionEstudianteProyectoGradoResult.Lapida(vinculo.getProyectoGrado(), vinculo.getAutor());
    }

    public static RemocionEstudianteProyectoGradoResult.Descartada toResultDescartada(
            EstudianteProyectoGradoDomain vinculo, Instant ocurridoEnVigente) {
        return new RemocionEstudianteProyectoGradoResult.Descartada(
                vinculo.getProyectoGrado(), vinculo.getAutor(), ocurridoEnVigente);
    }
}
