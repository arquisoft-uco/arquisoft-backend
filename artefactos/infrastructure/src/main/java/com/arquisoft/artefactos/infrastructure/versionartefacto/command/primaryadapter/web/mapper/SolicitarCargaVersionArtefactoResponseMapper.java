package com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.versionartefacto.command.result.CargaVersionArtefactoResult;
import com.arquisoft.artefactos.infrastructure.versionartefacto.command.primaryadapter.web.dto.SolicitarCargaVersionArtefactoResponseDTO;

public final class SolicitarCargaVersionArtefactoResponseMapper {

    private SolicitarCargaVersionArtefactoResponseMapper() {}

    public static SolicitarCargaVersionArtefactoResponseDTO toResponse(CargaVersionArtefactoResult result) {
        return new SolicitarCargaVersionArtefactoResponseDTO(
                result.carga(), result.url(), result.campos(), result.expiraEn());
    }
}
