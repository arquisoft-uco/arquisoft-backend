package com.arquisoft.artefactos.application.paqueteentregable.command.result.mapper;

import com.arquisoft.artefactos.application.paqueteentregable.command.result.PurgaPaquetesProyectoGradoResult;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;

public final class PurgaPaquetesProyectoGradoResultMapper {

    private PurgaPaquetesProyectoGradoResultMapper() {}

    public static PurgaPaquetesProyectoGradoResult toResult(PurgaPaquetesProyectoGradoDomain purga, int eliminados) {
        return new PurgaPaquetesProyectoGradoResult(purga.getProyectoGrado(), eliminados);
    }
}
