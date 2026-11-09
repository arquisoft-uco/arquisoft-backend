package com.arquisoft.artefactos.application.paqueteentregable.command.result.mapper;

import com.arquisoft.artefactos.application.paqueteentregable.command.result.PurgaPaquetesAnterioresResult;
import com.arquisoft.artefactos.domain.paqueteentregable.PurgaPaquetesAnterioresDomain;

public final class PurgaPaquetesAnterioresResultMapper {

    private PurgaPaquetesAnterioresResultMapper() {}

    public static PurgaPaquetesAnterioresResult toResult(PurgaPaquetesAnterioresDomain purga, int eliminados) {
        return new PurgaPaquetesAnterioresResult(purga.getProyectoGrado(), eliminados);
    }
}
