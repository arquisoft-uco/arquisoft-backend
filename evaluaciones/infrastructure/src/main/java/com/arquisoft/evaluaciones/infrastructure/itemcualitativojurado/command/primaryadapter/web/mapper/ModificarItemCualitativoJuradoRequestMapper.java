package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.ModificarItemCualitativoJuradoCommand;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.primaryadapter.web.dto.ModificarItemCualitativoJuradoRequestDTO;

import java.util.UUID;

public final class ModificarItemCualitativoJuradoRequestMapper {

    private ModificarItemCualitativoJuradoRequestMapper() {}

    public static ModificarItemCualitativoJuradoCommand toCommand(
            ModificarItemCualitativoJuradoRequestDTO dto, UUID itemId) {
        return ModificarItemCualitativoJuradoCommand.crear(itemId, dto.descripcion());
    }
}
