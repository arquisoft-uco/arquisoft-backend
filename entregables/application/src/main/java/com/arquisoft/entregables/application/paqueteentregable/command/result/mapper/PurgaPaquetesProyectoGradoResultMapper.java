package com.arquisoft.entregables.application.paqueteentregable.command.result.mapper;

import com.arquisoft.entregables.application.paqueteentregable.command.result.PurgaPaquetesProyectoGradoResult;
import com.arquisoft.entregables.domain.paqueteentregable.PurgaPaquetesProyectoGradoDomain;

public final class PurgaPaquetesProyectoGradoResultMapper {

    private PurgaPaquetesProyectoGradoResultMapper() {}

    public static PurgaPaquetesProyectoGradoResult toResult(PurgaPaquetesProyectoGradoDomain purga, int eliminados) {
        return new PurgaPaquetesProyectoGradoResult(purga.getProyectoGrado(), eliminados);
    }
}
