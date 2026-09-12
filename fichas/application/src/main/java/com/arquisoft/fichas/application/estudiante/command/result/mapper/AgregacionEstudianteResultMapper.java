package com.arquisoft.fichas.application.estudiante.command.result.mapper;

import com.arquisoft.fichas.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;

import java.time.Instant;

public final class AgregacionEstudianteResultMapper {

    private AgregacionEstudianteResultMapper() {}

    public static AgregacionEstudianteResult.Agregada toResultAgregada(EstudianteDomain estudiante) {
        return new AgregacionEstudianteResult.Agregada(estudiante.getId());
    }

    public static AgregacionEstudianteResult.Duplicada toResultDuplicada(EstudianteDomain estudiante) {
        return new AgregacionEstudianteResult.Duplicada(estudiante.getId());
    }

    public static AgregacionEstudianteResult.Descartada toResultDescartada(
            EstudianteDomain estudiante, Instant ocurridoEnVigente) {
        return new AgregacionEstudianteResult.Descartada(estudiante.getId(), ocurridoEnVigente);
    }
}
