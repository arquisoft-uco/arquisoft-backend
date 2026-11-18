package com.arquisoft.entregables.application.artefacto.command.result.mapper;

import com.arquisoft.entregables.application.artefacto.command.result.OmisionArtefactoResult;
import com.arquisoft.entregables.domain.artefacto.OmisionArtefactoDomain;

public final class OmisionArtefactoResultMapper {

    private OmisionArtefactoResultMapper() {}

    public static OmisionArtefactoResult.Omitida toResultOmitida(OmisionArtefactoDomain omision) {
        return new OmisionArtefactoResult.Omitida(omision.getArtefacto());
    }

    public static OmisionArtefactoResult.Anticipada toResultAnticipada(OmisionArtefactoDomain omision) {
        return new OmisionArtefactoResult.Anticipada(omision.getArtefacto());
    }

    public static OmisionArtefactoResult.Descartada toResultDescartada(OmisionArtefactoDomain omision) {
        return new OmisionArtefactoResult.Descartada(omision.getArtefacto());
    }
}
