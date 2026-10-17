package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.ModificarItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.primaryadapter.web.dto.ModificarItemCuantitativoJuradoRequestDTO;

import java.util.UUID;

public final class ModificarItemCuantitativoJuradoRequestMapper {

    private ModificarItemCuantitativoJuradoRequestMapper() {}

    public static ModificarItemCuantitativoJuradoCommand toCommand(
            ModificarItemCuantitativoJuradoRequestDTO dto, UUID itemId) {
        return ModificarItemCuantitativoJuradoCommand.crear(itemId, dto.descripcion());
    }
}
