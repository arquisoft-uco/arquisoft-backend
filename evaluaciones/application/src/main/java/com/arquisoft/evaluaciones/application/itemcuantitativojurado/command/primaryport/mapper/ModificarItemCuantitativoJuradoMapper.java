package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.ModificarItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ModificacionItemCuantitativoJuradoDomain;

public final class ModificarItemCuantitativoJuradoMapper {

    private ModificarItemCuantitativoJuradoMapper() {}

    public static ModificacionItemCuantitativoJuradoDomain toDomain(
            ModificarItemCuantitativoJuradoCommand command) {
        return ModificacionItemCuantitativoJuradoDomain.crear(
                command.itemCuantitativoJurado(), command.descripcion());
    }
}
