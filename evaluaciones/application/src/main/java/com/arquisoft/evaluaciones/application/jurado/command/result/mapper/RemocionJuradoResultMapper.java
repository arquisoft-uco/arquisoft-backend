package com.arquisoft.evaluaciones.application.jurado.command.result.mapper;

import com.arquisoft.evaluaciones.application.jurado.command.result.RemocionJuradoResult;
import com.arquisoft.evaluaciones.domain.jurado.JuradoDomain;

import java.time.Instant;

public final class RemocionJuradoResultMapper {

    private RemocionJuradoResultMapper() {}

    public static RemocionJuradoResult.Removida toResultRemovida(JuradoDomain jurado) {
        return new RemocionJuradoResult.Removida(jurado.getId());
    }

    public static RemocionJuradoResult.Lapida toResultLapida(JuradoDomain jurado) {
        return new RemocionJuradoResult.Lapida(jurado.getId());
    }

    public static RemocionJuradoResult.Descartada toResultDescartada(
            JuradoDomain jurado, Instant ocurridoEnVigente) {
        return new RemocionJuradoResult.Descartada(jurado.getId(), ocurridoEnVigente);
    }
}
