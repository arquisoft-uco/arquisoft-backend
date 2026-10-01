package com.arquisoft.proyectos.application.coordinador.command.result.mapper;

import com.arquisoft.proyectos.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;

import java.time.Instant;

public final class AgregacionCoordinadorResultMapper {

    private AgregacionCoordinadorResultMapper() {}

    public static AgregacionCoordinadorResult.Agregada toResultAgregada(CoordinadorDomain coordinador) {
        return new AgregacionCoordinadorResult.Agregada(coordinador.getId());
    }

    public static AgregacionCoordinadorResult.Reactivada toResultReactivada(CoordinadorDomain coordinador) {
        return new AgregacionCoordinadorResult.Reactivada(coordinador.getId());
    }

    public static AgregacionCoordinadorResult.Duplicada toResultDuplicada(CoordinadorDomain coordinador) {
        return new AgregacionCoordinadorResult.Duplicada(coordinador.getId());
    }

    public static AgregacionCoordinadorResult.Descartada toResultDescartada(
            CoordinadorDomain coordinador, Instant ocurridoEnVigente) {
        return new AgregacionCoordinadorResult.Descartada(coordinador.getId(), ocurridoEnVigente);
    }
}
