package com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativoasesor.command.secondaryport.entity.ItemCualitativoAsesorEntity;
import com.arquisoft.evaluaciones.domain.itemcualitativoasesor.ItemCualitativoAsesorDomain;

public final class ItemCualitativoAsesorMapper {

    private ItemCualitativoAsesorMapper() {}

    public static ItemCualitativoAsesorEntity toEntity(ItemCualitativoAsesorDomain domain) {
        return new ItemCualitativoAsesorEntity(domain.getId(), domain.getNombre(), domain.getDescripcion());
    }
}
