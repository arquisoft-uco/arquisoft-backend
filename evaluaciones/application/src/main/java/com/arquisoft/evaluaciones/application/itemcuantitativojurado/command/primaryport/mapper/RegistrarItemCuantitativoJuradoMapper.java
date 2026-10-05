package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.primaryport.model.RegistrarItemCuantitativoJuradoCommand;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;

public final class RegistrarItemCuantitativoJuradoMapper {

    private RegistrarItemCuantitativoJuradoMapper() {}

    public static ItemCuantitativoJuradoDomain toDomain(
            RegistrarItemCuantitativoJuradoCommand command) {
        return ItemCuantitativoJuradoDomain.crear(
                command.nombre(), command.descripcion(), command.categoria(), command.valor());
    }
}
