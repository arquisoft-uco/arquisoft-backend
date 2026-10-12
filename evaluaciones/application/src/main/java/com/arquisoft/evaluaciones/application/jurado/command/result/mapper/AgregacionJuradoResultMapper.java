package com.arquisoft.evaluaciones.application.jurado.command.result.mapper;

import com.arquisoft.evaluaciones.application.jurado.command.result.AgregacionJuradoResult;
import com.arquisoft.evaluaciones.domain.jurado.JuradoDomain;

import java.time.Instant;

public final class AgregacionJuradoResultMapper {

    private AgregacionJuradoResultMapper() {}

    public static AgregacionJuradoResult.Agregada toResultAgregada(JuradoDomain jurado) {
        return new AgregacionJuradoResult.Agregada(jurado.getId());
    }

    public static AgregacionJuradoResult.Reactivada toResultReactivada(JuradoDomain jurado) {
        return new AgregacionJuradoResult.Reactivada(jurado.getId());
    }

    public static AgregacionJuradoResult.Duplicada toResultDuplicada(JuradoDomain jurado) {
        return new AgregacionJuradoResult.Duplicada(jurado.getId());
    }

    public static AgregacionJuradoResult.Descartada toResultDescartada(
            JuradoDomain jurado, Instant ocurridoEnVigente) {
        return new AgregacionJuradoResult.Descartada(jurado.getId(), ocurridoEnVigente);
    }
}
