package com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.versionartefacto.query.readmodel.EnlaceDescargaVersionArtefactoReadModel;
import com.arquisoft.artefactos.infrastructure.versionartefacto.query.primaryadapter.web.dto.EnlaceDescargaVersionArtefactoResponseDTO;

public final class EnlaceDescargaVersionArtefactoResponseMapper {

    private EnlaceDescargaVersionArtefactoResponseMapper() {}

    public static EnlaceDescargaVersionArtefactoResponseDTO toResponse(EnlaceDescargaVersionArtefactoReadModel readModel) {
        return new EnlaceDescargaVersionArtefactoResponseDTO(readModel.url(), readModel.expiraEn());
    }
}
