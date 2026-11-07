package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.RegistrarItemCuantitativoAsesorCommand;
import com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.command.primaryadapter.web.dto.RegistrarItemCuantitativoAsesorRequestDTO;

public final class RegistrarItemCuantitativoAsesorRequestMapper {

    private RegistrarItemCuantitativoAsesorRequestMapper() {}

    public static RegistrarItemCuantitativoAsesorCommand toCommand(
            RegistrarItemCuantitativoAsesorRequestDTO dto) {
        return RegistrarItemCuantitativoAsesorCommand.crear(
                dto.nombre(), dto.descripcion(), dto.categoria(), dto.valor());
    }
}
