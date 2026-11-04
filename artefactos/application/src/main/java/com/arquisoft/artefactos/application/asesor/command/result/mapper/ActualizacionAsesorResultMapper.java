package com.arquisoft.artefactos.application.asesor.command.result.mapper;

import com.arquisoft.artefactos.application.asesor.command.result.ActualizacionAsesorResult;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;

import java.time.Instant;

public final class ActualizacionAsesorResultMapper {

    private ActualizacionAsesorResultMapper() {}

    public static ActualizacionAsesorResult.Actualizada toResultActualizada(AsesorDomain asesor) {
        return new ActualizacionAsesorResult.Actualizada(asesor.getId());
    }

    public static ActualizacionAsesorResult.Descartada toResultDescartada(
            AsesorDomain asesor, Instant ocurridoEnVigente) {
        return new ActualizacionAsesorResult.Descartada(asesor.getId(), ocurridoEnVigente);
    }

    public static ActualizacionAsesorResult.NoReplicado toResultNoReplicado(AsesorDomain asesor) {
        return new ActualizacionAsesorResult.NoReplicado(asesor.getId());
    }
}
