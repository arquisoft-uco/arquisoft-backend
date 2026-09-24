package com.arquisoft.fichas.application.asesorficha.command.result.mapper;

import com.arquisoft.fichas.application.asesorficha.command.result.RemocionAsesorFichaResult;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;

import java.time.Instant;

public final class RemocionAsesorFichaResultMapper {

    private RemocionAsesorFichaResultMapper() {}

    public static RemocionAsesorFichaResult.Removida toResultRemovida(AsesorFichaDomain asesorFicha) {
        return new RemocionAsesorFichaResult.Removida(asesorFicha.getId());
    }

    public static RemocionAsesorFichaResult.Lapida toResultLapida(AsesorFichaDomain asesorFicha) {
        return new RemocionAsesorFichaResult.Lapida(asesorFicha.getId());
    }

    public static RemocionAsesorFichaResult.Descartada toResultDescartada(
            AsesorFichaDomain asesorFicha, Instant ocurridoEnVigente) {
        return new RemocionAsesorFichaResult.Descartada(asesorFicha.getId(), ocurridoEnVigente);
    }
}
