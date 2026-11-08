package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.primaryport.model.ModificarItemCualitativoAsesorCommand;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ModificacionItemCualitativoAsesorDomain;

public final class ModificarItemCualitativoAsesorMapper {

    private ModificarItemCualitativoAsesorMapper() {}

    public static ModificacionItemCualitativoAsesorDomain toDomain(
            ModificarItemCualitativoAsesorCommand command) {
        return ModificacionItemCualitativoAsesorDomain.crear(
                command.itemCualitativoAsesor(), command.descripcion());
    }
}
