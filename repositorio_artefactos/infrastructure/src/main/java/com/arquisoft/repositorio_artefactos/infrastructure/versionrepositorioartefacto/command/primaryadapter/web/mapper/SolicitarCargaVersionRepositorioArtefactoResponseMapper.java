package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.CargaVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.command.primaryadapter.web.dto.SolicitarCargaVersionRepositorioArtefactoResponseDTO;

public final class SolicitarCargaVersionRepositorioArtefactoResponseMapper {

    private SolicitarCargaVersionRepositorioArtefactoResponseMapper() {}

    public static SolicitarCargaVersionRepositorioArtefactoResponseDTO toResponse(
            CargaVersionRepositorioArtefactoResult result) {
        return new SolicitarCargaVersionRepositorioArtefactoResponseDTO(
                result.carga(), result.url(), result.campos(), result.expiraEn());
    }
}
