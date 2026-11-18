package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.RegistrarItemCualitativoAsesorCommand;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ItemCualitativoAsesorDomain;

public final class RegistrarItemCualitativoAsesorMapper {

    private RegistrarItemCualitativoAsesorMapper() {}

    public static ItemCualitativoAsesorDomain toDomain(RegistrarItemCualitativoAsesorCommand command) {
        return ItemCualitativoAsesorDomain.crear(command.nombre(), command.descripcion());
    }
}
