package com.arquisoft.fichas.application.estudiante.command.result.mapper;

import com.arquisoft.fichas.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;

import java.time.Instant;

public final class ActualizacionEstudianteResultMapper {

    private ActualizacionEstudianteResultMapper() {}

    public static ActualizacionEstudianteResult.Actualizada toResultActualizada(EstudianteDomain estudiante) {
        return new ActualizacionEstudianteResult.Actualizada(estudiante.getId());
    }

    public static ActualizacionEstudianteResult.Descartada toResultDescartada(
            EstudianteDomain estudiante, Instant ocurridoEnVigente) {
        return new ActualizacionEstudianteResult.Descartada(estudiante.getId(), ocurridoEnVigente);
    }

    public static ActualizacionEstudianteResult.NoReplicado toResultNoReplicado(EstudianteDomain estudiante) {
        return new ActualizacionEstudianteResult.NoReplicado(estudiante.getId());
    }
}
