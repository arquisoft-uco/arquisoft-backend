package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.ModificarItemCualitativoAsesorCommand;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web.dto.ModificarItemCualitativoAsesorRequestDTO;

import java.util.UUID;

public final class ModificarItemCualitativoAsesorRequestMapper {

    private ModificarItemCualitativoAsesorRequestMapper() {}

    public static ModificarItemCualitativoAsesorCommand toCommand(
            ModificarItemCualitativoAsesorRequestDTO dto, UUID itemId) {
        return ModificarItemCualitativoAsesorCommand.crear(itemId, dto.descripcion());
    }
}
