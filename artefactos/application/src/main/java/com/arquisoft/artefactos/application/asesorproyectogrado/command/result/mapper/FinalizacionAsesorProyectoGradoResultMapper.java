package com.arquisoft.artefactos.application.asesorproyectogrado.command.result.mapper;

import com.arquisoft.artefactos.application.asesorproyectogrado.command.result.FinalizacionAsesorProyectoGradoResult;
import com.arquisoft.artefactos.domain.asesorproyectogrado.AsesorProyectoGradoDomain;

import java.time.Instant;

public final class FinalizacionAsesorProyectoGradoResultMapper {

    private FinalizacionAsesorProyectoGradoResultMapper() {}

    public static FinalizacionAsesorProyectoGradoResult.Finalizada toResultFinalizada(
            AsesorProyectoGradoDomain vinculo) {
        return new FinalizacionAsesorProyectoGradoResult.Finalizada(
                vinculo.getAsesor(), vinculo.getProyectoGrado());
    }

    public static FinalizacionAsesorProyectoGradoResult.Lapida toResultLapida(AsesorProyectoGradoDomain vinculo) {
        return new FinalizacionAsesorProyectoGradoResult.Lapida(vinculo.getAsesor(), vinculo.getProyectoGrado());
    }

    public static FinalizacionAsesorProyectoGradoResult.Descartada toResultDescartada(
            AsesorProyectoGradoDomain vinculo, Instant ocurridoEnVigente) {
        return new FinalizacionAsesorProyectoGradoResult.Descartada(
                vinculo.getAsesor(), vinculo.getProyectoGrado(), ocurridoEnVigente);
    }
}
