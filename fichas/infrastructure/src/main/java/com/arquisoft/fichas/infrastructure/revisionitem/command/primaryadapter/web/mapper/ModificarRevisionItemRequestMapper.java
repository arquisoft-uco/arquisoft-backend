package com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web.mapper;

import com.arquisoft.fichas.application.revisionitem.command.primaryport.model.ModificarRevisionItemCommand;
import com.arquisoft.fichas.infrastructure.revisionitem.command.primaryadapter.web.dto.ModificarRevisionItemRequestDTO;

import java.util.UUID;

public final class ModificarRevisionItemRequestMapper {

    private ModificarRevisionItemRequestMapper() {}

    public static ModificarRevisionItemCommand toCommand(
            ModificarRevisionItemRequestDTO dto, UUID item, UUID asesorFicha) {
        return ModificarRevisionItemCommand.crear(item, dto.estadoRevision(), asesorFicha);
    }
}
