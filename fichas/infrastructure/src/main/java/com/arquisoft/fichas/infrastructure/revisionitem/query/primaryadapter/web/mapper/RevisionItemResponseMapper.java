package com.arquisoft.fichas.infrastructure.revisionitem.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.revisionitem.query.readmodel.RevisionItemReadModel;
import com.arquisoft.fichas.infrastructure.revisionitem.query.primaryadapter.web.dto.RevisionItemResponseDTO;

public final class RevisionItemResponseMapper {

    private RevisionItemResponseMapper() {}

    public static RevisionItemResponseDTO toResponse(RevisionItemReadModel readModel) {
        return new RevisionItemResponseDTO(
                readModel.id(),
                readModel.item(),
                readModel.estadoRevision(),
                readModel.estadoRevisionNombre(),
                readModel.fechaCreacion());
    }
}
