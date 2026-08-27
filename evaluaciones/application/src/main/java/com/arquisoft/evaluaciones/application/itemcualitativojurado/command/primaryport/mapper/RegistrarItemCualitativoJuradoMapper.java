package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.primaryport.model.RegistrarItemCualitativoJuradoCommand;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ItemCualitativoJuradoDomain;

public final class RegistrarItemCualitativoJuradoMapper {

    private RegistrarItemCualitativoJuradoMapper() {}

    public static ItemCualitativoJuradoDomain toDomain(
            RegistrarItemCualitativoJuradoCommand command) {
        return ItemCualitativoJuradoDomain.crear(command.nombre(), command.descripcion());
    }
}
