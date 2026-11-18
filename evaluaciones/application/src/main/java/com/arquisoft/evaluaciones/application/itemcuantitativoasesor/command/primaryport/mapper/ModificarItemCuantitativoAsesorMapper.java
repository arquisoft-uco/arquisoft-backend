package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.primaryport.model.ModificarItemCuantitativoAsesorCommand;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ModificacionItemCuantitativoAsesorDomain;

public final class ModificarItemCuantitativoAsesorMapper {

    private ModificarItemCuantitativoAsesorMapper() {}

    public static ModificacionItemCuantitativoAsesorDomain toDomain(
            ModificarItemCuantitativoAsesorCommand command) {
        return ModificacionItemCuantitativoAsesorDomain.crear(
                command.itemCuantitativoAsesor(), command.descripcion());
    }
}
