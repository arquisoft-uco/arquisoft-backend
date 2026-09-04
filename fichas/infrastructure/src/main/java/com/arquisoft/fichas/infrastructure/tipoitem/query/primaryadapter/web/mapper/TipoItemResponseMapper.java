package com.arquisoft.fichas.infrastructure.tipoitem.query.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.tipoitem.query.readmodel.TipoItemReadModel;
import com.arquisoft.fichas.infrastructure.tipoitem.query.primaryadapter.web.dto.TipoItemResponseDTO;

public final class TipoItemResponseMapper {

    private TipoItemResponseMapper() {}

    public static TipoItemResponseDTO toResponse(TipoItemReadModel readModel) {
        return new TipoItemResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
