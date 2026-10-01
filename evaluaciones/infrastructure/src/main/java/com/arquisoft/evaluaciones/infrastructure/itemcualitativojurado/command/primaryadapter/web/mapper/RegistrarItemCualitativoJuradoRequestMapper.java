package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.RegistrarItemCualitativoJuradoCommand;
import com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.command.primaryadapter.web.dto.RegistrarItemCualitativoJuradoRequestDTO;

public final class RegistrarItemCualitativoJuradoRequestMapper {

    private RegistrarItemCualitativoJuradoRequestMapper() {}

    public static RegistrarItemCualitativoJuradoCommand toCommand(
            RegistrarItemCualitativoJuradoRequestDTO dto) {
        return RegistrarItemCualitativoJuradoCommand.crear(dto.nombre(), dto.descripcion());
    }
}
