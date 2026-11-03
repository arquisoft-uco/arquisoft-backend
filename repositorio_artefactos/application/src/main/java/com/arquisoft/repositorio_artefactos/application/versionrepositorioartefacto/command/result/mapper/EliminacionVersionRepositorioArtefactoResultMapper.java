package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.EliminacionVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.EliminacionVersionRepositorioArtefactoDomain;

public final class EliminacionVersionRepositorioArtefactoResultMapper {

    private EliminacionVersionRepositorioArtefactoResultMapper() {}

    public static EliminacionVersionRepositorioArtefactoResult.Eliminada toResultEliminada(
            EliminacionVersionRepositorioArtefactoDomain eliminacion) {
        return new EliminacionVersionRepositorioArtefactoResult.Eliminada(eliminacion.getVersion());
    }

    public static EliminacionVersionRepositorioArtefactoResult.Inexistente toResultInexistente(
            EliminacionVersionRepositorioArtefactoDomain eliminacion) {
        return new EliminacionVersionRepositorioArtefactoResult.Inexistente(eliminacion.getVersion());
    }
}
