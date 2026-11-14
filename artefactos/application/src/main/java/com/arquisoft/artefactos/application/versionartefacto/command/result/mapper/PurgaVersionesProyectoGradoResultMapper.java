package com.arquisoft.artefactos.application.versionartefacto.command.result.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.result.PurgaVersionesProyectoGradoResult;
import com.arquisoft.artefactos.domain.versionartefacto.PurgaVersionesProyectoGradoDomain;

public final class PurgaVersionesProyectoGradoResultMapper {

    private PurgaVersionesProyectoGradoResultMapper() {}

    public static PurgaVersionesProyectoGradoResult toResult(PurgaVersionesProyectoGradoDomain purga, int archivos) {
        return new PurgaVersionesProyectoGradoResult(purga.getProyectoGrado(), archivos);
    }
}
