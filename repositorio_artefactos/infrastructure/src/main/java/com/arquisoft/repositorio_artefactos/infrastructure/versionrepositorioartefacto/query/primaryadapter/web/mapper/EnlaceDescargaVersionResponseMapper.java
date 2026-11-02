package com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.mapper;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.readmodel.EnlaceDescargaVersionReadModel;
import com.arquisoft.repositorio_artefactos.infrastructure.versionrepositorioartefacto.query.primaryadapter.web.dto.EnlaceDescargaVersionResponseDTO;

public final class EnlaceDescargaVersionResponseMapper {

    private EnlaceDescargaVersionResponseMapper() {}

    public static EnlaceDescargaVersionResponseDTO toResponse(EnlaceDescargaVersionReadModel readModel) {
        return new EnlaceDescargaVersionResponseDTO(readModel.url(), readModel.expiraEn());
    }
}
