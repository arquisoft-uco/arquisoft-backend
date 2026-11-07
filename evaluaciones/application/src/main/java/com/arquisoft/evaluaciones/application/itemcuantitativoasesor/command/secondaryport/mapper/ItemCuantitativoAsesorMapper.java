package com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.secondaryport.entity.ItemCuantitativoAsesorEntity;
import com.arquisoft.evaluaciones.domain.itemcuantitativoasesor.ItemCuantitativoAsesorDomain;

public final class ItemCuantitativoAsesorMapper {

    private ItemCuantitativoAsesorMapper() {}

    public static ItemCuantitativoAsesorEntity toEntity(ItemCuantitativoAsesorDomain domain) {
        return new ItemCuantitativoAsesorEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getDescripcion(),
                domain.getCategoria(),
                domain.getValor());
    }
}
