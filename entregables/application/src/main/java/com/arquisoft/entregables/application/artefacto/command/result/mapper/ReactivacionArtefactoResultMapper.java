package com.arquisoft.entregables.application.artefacto.command.result.mapper;

import com.arquisoft.entregables.application.artefacto.command.result.ReactivacionArtefactoResult;
import com.arquisoft.entregables.domain.artefacto.ReactivacionArtefactoDomain;

public final class ReactivacionArtefactoResultMapper {

    private ReactivacionArtefactoResultMapper() {}

    public static ReactivacionArtefactoResult.Reactivada toResultReactivada(ReactivacionArtefactoDomain reactivacion) {
        return new ReactivacionArtefactoResult.Reactivada(reactivacion.getArtefacto());
    }

    public static ReactivacionArtefactoResult.Anticipada toResultAnticipada(
            ReactivacionArtefactoDomain reactivacion) {
        return new ReactivacionArtefactoResult.Anticipada(reactivacion.getArtefacto());
    }

    public static ReactivacionArtefactoResult.Descartada toResultDescartada(ReactivacionArtefactoDomain reactivacion) {
        return new ReactivacionArtefactoResult.Descartada(reactivacion.getArtefacto());
    }
}
