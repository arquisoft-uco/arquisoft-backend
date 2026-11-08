package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.RegistrarItemCualitativoAsesorCommand;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.command.primaryadapter.web.dto.RegistrarItemCualitativoAsesorRequestDTO;

public final class RegistrarItemCualitativoAsesorRequestMapper {

    private RegistrarItemCualitativoAsesorRequestMapper() {}

    public static RegistrarItemCualitativoAsesorCommand toCommand(
            RegistrarItemCualitativoAsesorRequestDTO dto) {
        return RegistrarItemCualitativoAsesorCommand.crear(dto.nombre(), dto.descripcion());
    }
}
