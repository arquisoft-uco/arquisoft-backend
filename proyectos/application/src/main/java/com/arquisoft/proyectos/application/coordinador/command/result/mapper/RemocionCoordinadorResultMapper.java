package com.arquisoft.proyectos.application.coordinador.command.result.mapper;

import com.arquisoft.proyectos.application.coordinador.command.result.RemocionCoordinadorResult;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;

import java.time.Instant;

public final class RemocionCoordinadorResultMapper {

    private RemocionCoordinadorResultMapper() {}

    public static RemocionCoordinadorResult.Removida toResultRemovida(CoordinadorDomain coordinador) {
        return new RemocionCoordinadorResult.Removida(coordinador.getId());
    }

    public static RemocionCoordinadorResult.Lapida toResultLapida(CoordinadorDomain coordinador) {
        return new RemocionCoordinadorResult.Lapida(coordinador.getId());
    }

    public static RemocionCoordinadorResult.Descartada toResultDescartada(
            CoordinadorDomain coordinador, Instant ocurridoEnVigente) {
        return new RemocionCoordinadorResult.Descartada(coordinador.getId(), ocurridoEnVigente);
    }
}
