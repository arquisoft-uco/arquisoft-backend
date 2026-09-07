package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.RegistrarItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.command.primaryadapter.web.dto.RegistrarItemCuantitativoJuradoRequestDTO;

public final class RegistrarItemCuantitativoJuradoRequestMapper {

    private RegistrarItemCuantitativoJuradoRequestMapper() {}

    public static RegistrarItemCuantitativoJuradoCommand toCommand(
            RegistrarItemCuantitativoJuradoRequestDTO dto) {
        return RegistrarItemCuantitativoJuradoCommand.crear(
                dto.nombre(), dto.descripcion(), dto.categoria(), dto.valor());
    }
}
