package com.arquisoft.entregables.application.artefacto.command.result.mapper;

import com.arquisoft.entregables.application.artefacto.command.result.PurgaArtefactosProyectoGradoResult;
import com.arquisoft.entregables.domain.artefacto.PurgaArtefactosProyectoGradoDomain;

public final class PurgaArtefactosProyectoGradoResultMapper {

    private PurgaArtefactosProyectoGradoResultMapper() {}

    public static PurgaArtefactosProyectoGradoResult toResult(PurgaArtefactosProyectoGradoDomain purga, int eliminadas) {
        return new PurgaArtefactosProyectoGradoResult(purga.getProyectoGrado(), eliminadas);
    }
}
