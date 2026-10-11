package com.arquisoft.fichas.application.asesorficha.command.result.mapper;

import com.arquisoft.fichas.application.asesorficha.command.result.AgregacionAsesorFichaResult;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;

import java.time.Instant;

public final class AgregacionAsesorFichaResultMapper {

    private AgregacionAsesorFichaResultMapper() {}

    public static AgregacionAsesorFichaResult.Agregada toResultAgregada(AsesorFichaDomain asesorFicha) {
        return new AgregacionAsesorFichaResult.Agregada(asesorFicha.getId());
    }

    public static AgregacionAsesorFichaResult.Reactivada toResultReactivada(AsesorFichaDomain asesorFicha) {
        return new AgregacionAsesorFichaResult.Reactivada(asesorFicha.getId());
    }

    public static AgregacionAsesorFichaResult.Duplicada toResultDuplicada(AsesorFichaDomain asesorFicha) {
        return new AgregacionAsesorFichaResult.Duplicada(asesorFicha.getId());
    }

    public static AgregacionAsesorFichaResult.Descartada toResultDescartada(
            AsesorFichaDomain asesorFicha, Instant ocurridoEnVigente) {
        return new AgregacionAsesorFichaResult.Descartada(asesorFicha.getId(), ocurridoEnVigente);
    }
}
