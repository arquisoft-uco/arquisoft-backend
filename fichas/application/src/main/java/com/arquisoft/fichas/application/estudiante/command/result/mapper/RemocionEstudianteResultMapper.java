package com.arquisoft.fichas.application.estudiante.command.result.mapper;

import com.arquisoft.fichas.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;

import java.time.Instant;

public final class RemocionEstudianteResultMapper {

    private RemocionEstudianteResultMapper() {}

    public static RemocionEstudianteResult.Removida toResultRemovida(EstudianteDomain estudiante) {
        return new RemocionEstudianteResult.Removida(estudiante.getId());
    }

    public static RemocionEstudianteResult.Lapida toResultLapida(EstudianteDomain estudiante) {
        return new RemocionEstudianteResult.Lapida(estudiante.getId());
    }

    public static RemocionEstudianteResult.Descartada toResultDescartada(
            EstudianteDomain estudiante, Instant ocurridoEnVigente) {
        return new RemocionEstudianteResult.Descartada(estudiante.getId(), ocurridoEnVigente);
    }
}
