package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.ModificarItemCuantitativoAsesorCommand;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web.dto.ModificarItemCuantitativoAsesorRequestDTO;

import java.util.UUID;

public final class ModificarItemCuantitativoAsesorRequestMapper {

    private ModificarItemCuantitativoAsesorRequestMapper() {}

    public static ModificarItemCuantitativoAsesorCommand toCommand(
            ModificarItemCuantitativoAsesorRequestDTO dto, UUID itemId) {
        return ModificarItemCuantitativoAsesorCommand.crear(itemId, dto.descripcion());
    }
}
