package com.arquisoft.proyectos.application.asesor.command.result.mapper;

import com.arquisoft.proyectos.application.asesor.command.result.RemocionAsesorResult;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;

import java.time.Instant;

public final class RemocionAsesorResultMapper {

    private RemocionAsesorResultMapper() {}

    public static RemocionAsesorResult.Removida toResultRemovida(AsesorDomain asesor) {
        return new RemocionAsesorResult.Removida(asesor.getId());
    }

    public static RemocionAsesorResult.Lapida toResultLapida(AsesorDomain asesor) {
        return new RemocionAsesorResult.Lapida(asesor.getId());
    }

    public static RemocionAsesorResult.Descartada toResultDescartada(
            AsesorDomain asesor, Instant ocurridoEnVigente) {
        return new RemocionAsesorResult.Descartada(asesor.getId(), ocurridoEnVigente);
    }
}
