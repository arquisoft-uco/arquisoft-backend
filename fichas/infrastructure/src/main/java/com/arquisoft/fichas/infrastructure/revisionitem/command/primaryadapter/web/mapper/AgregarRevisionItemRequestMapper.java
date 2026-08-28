package com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.AgregarRevisionItemCommand;
import com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web.dto.AgregarRevisionItemRequestDTO;

import java.util.UUID;

public final class AgregarRevisionItemRequestMapper {

    private AgregarRevisionItemRequestMapper() {}

    public static AgregarRevisionItemCommand toCommand(
            AgregarRevisionItemRequestDTO dto, UUID item, UUID asesorFicha) {
        return AgregarRevisionItemCommand.crear(item, dto.estadoRevision(), asesorFicha);
    }
}
