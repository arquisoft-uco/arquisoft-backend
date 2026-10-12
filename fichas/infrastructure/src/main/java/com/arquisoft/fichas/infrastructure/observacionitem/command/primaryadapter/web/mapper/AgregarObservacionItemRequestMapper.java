package com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.observacionitem.command.primaryport.model.AgregarObservacionItemCommand;
import com.arquisoft.fichas.infrastructure.observacionitem.command.primaryadapter.web.dto.AgregarObservacionItemRequestDTO;

import java.util.UUID;

public final class AgregarObservacionItemRequestMapper {

    private AgregarObservacionItemRequestMapper() {}

    public static AgregarObservacionItemCommand toCommand(
            AgregarObservacionItemRequestDTO dto, UUID revisionItemId, UUID asesorFichaId) {
        return AgregarObservacionItemCommand.crear(revisionItemId, dto.observacion(), asesorFichaId);
    }
}
