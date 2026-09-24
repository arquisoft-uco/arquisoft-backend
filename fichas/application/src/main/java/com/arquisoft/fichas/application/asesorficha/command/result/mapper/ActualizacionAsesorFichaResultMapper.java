package com.arquisoft.fichas.application.asesorficha.command.result.mapper;

import com.arquisoft.fichas.application.asesorficha.command.result.ActualizacionAsesorFichaResult;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;

import java.time.Instant;

public final class ActualizacionAsesorFichaResultMapper {

    private ActualizacionAsesorFichaResultMapper() {}

    public static ActualizacionAsesorFichaResult.Actualizada toResultActualizada(AsesorFichaDomain asesorFicha) {
        return new ActualizacionAsesorFichaResult.Actualizada(asesorFicha.getId());
    }

    public static ActualizacionAsesorFichaResult.Descartada toResultDescartada(
            AsesorFichaDomain asesorFicha, Instant ocurridoEnVigente) {
        return new ActualizacionAsesorFichaResult.Descartada(asesorFicha.getId(), ocurridoEnVigente);
    }

    public static ActualizacionAsesorFichaResult.NoReplicado toResultNoReplicado(AsesorFichaDomain asesorFicha) {
        return new ActualizacionAsesorFichaResult.NoReplicado(asesorFicha.getId());
    }
}
