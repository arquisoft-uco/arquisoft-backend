package com.arquisoft.proyectos.application.coordinador.command.result.mapper;

import com.arquisoft.proyectos.application.coordinador.command.result.ActualizacionCoordinadorResult;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;

import java.time.Instant;

public final class ActualizacionCoordinadorResultMapper {

    private ActualizacionCoordinadorResultMapper() {}

    public static ActualizacionCoordinadorResult.Actualizada toResultActualizada(CoordinadorDomain coordinador) {
        return new ActualizacionCoordinadorResult.Actualizada(coordinador.getId());
    }

    public static ActualizacionCoordinadorResult.Descartada toResultDescartada(
            CoordinadorDomain coordinador, Instant ocurridoEnVigente) {
        return new ActualizacionCoordinadorResult.Descartada(coordinador.getId(), ocurridoEnVigente);
    }

    public static ActualizacionCoordinadorResult.NoReplicado toResultNoReplicado(CoordinadorDomain coordinador) {
        return new ActualizacionCoordinadorResult.NoReplicado(coordinador.getId());
    }
}
