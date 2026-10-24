package com.arquisoft.mapas_ruta.infrastructure.estadoitem.query.primaryadapter.web.mapper;

import com.arquisoft.mapas_ruta.application.estadoitem.query.readmodel.EstadoItemReadModel;
import com.arquisoft.mapas_ruta.infrastructure.estadoitem.query.primaryadapter.web.dto.EstadoItemResponseDTO;

public final class EstadoItemResponseMapper {

    private EstadoItemResponseMapper() {}

    public static EstadoItemResponseDTO toResponse(EstadoItemReadModel readModel) {
        return new EstadoItemResponseDTO(
                readModel.id(),
                readModel.nombre(),
                readModel.descripcion());
    }
}
