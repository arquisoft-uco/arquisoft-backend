package com.arquisoft.artefactos.application.asesorproyectogrado.command.result.mapper;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.AgregacionAsesorProyectoGradoResult;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;

import java.time.Instant;

public final class AgregacionAsesorProyectoGradoResultMapper {

    private AgregacionAsesorProyectoGradoResultMapper() {}

    public static AgregacionAsesorProyectoGradoResult.Agregada toResultAgregada(AsesorProyectoGradoDomain vinculo) {
        return new AgregacionAsesorProyectoGradoResult.Agregada(vinculo.getAsesor(), vinculo.getProyectoGrado());
    }

    public static AgregacionAsesorProyectoGradoResult.Reactivada toResultReactivada(
            AsesorProyectoGradoDomain vinculo) {
        return new AgregacionAsesorProyectoGradoResult.Reactivada(vinculo.getAsesor(), vinculo.getProyectoGrado());
    }

    public static AgregacionAsesorProyectoGradoResult.Duplicada toResultDuplicada(
            AsesorProyectoGradoDomain vinculo) {
        return new AgregacionAsesorProyectoGradoResult.Duplicada(vinculo.getAsesor(), vinculo.getProyectoGrado());
    }

    public static AgregacionAsesorProyectoGradoResult.Descartada toResultDescartada(
            AsesorProyectoGradoDomain vinculo, Instant ocurridoEnVigente) {
        return new AgregacionAsesorProyectoGradoResult.Descartada(
                vinculo.getAsesor(), vinculo.getProyectoGrado(), ocurridoEnVigente);
    }
}
