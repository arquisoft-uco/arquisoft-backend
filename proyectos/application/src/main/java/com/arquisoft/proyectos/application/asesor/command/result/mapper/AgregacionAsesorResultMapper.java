package com.arquisoft.proyectos.application.asesor.command.result.mapper;

import com.arquisoft.proyectos.application.asesor.command.result.AgregacionAsesorResult;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;

import java.time.Instant;

public final class AgregacionAsesorResultMapper {

    private AgregacionAsesorResultMapper() {}

    public static AgregacionAsesorResult.Agregada toResultAgregada(AsesorDomain asesor) {
        return new AgregacionAsesorResult.Agregada(asesor.getId());
    }

    public static AgregacionAsesorResult.Duplicada toResultDuplicada(AsesorDomain asesor) {
        return new AgregacionAsesorResult.Duplicada(asesor.getId());
    }

    public static AgregacionAsesorResult.Descartada toResultDescartada(
            AsesorDomain asesor, Instant ocurridoEnVigente) {
        return new AgregacionAsesorResult.Descartada(asesor.getId(), ocurridoEnVigente);
    }
}
