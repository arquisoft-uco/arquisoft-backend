package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.ModificarItemCualitativoJuradoCommand;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ModificacionItemCualitativoJuradoDomain;

public final class ModificarItemCualitativoJuradoMapper {

    private ModificarItemCualitativoJuradoMapper() {}

    public static ModificacionItemCualitativoJuradoDomain toDomain(
            ModificarItemCualitativoJuradoCommand command) {
        return ModificacionItemCualitativoJuradoDomain.crear(
                command.itemCualitativoJurado(), command.descripcion());
    }
}
