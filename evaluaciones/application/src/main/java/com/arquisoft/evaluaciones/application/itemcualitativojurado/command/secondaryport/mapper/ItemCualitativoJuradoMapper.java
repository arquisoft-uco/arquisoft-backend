package com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcualitativojurado.command.secondaryport.entity.ItemCualitativoJuradoEntity;
import com.arquisoft.evaluaciones.domain.itemcualitativojurado.ItemCualitativoJuradoDomain;

public final class ItemCualitativoJuradoMapper {

    private ItemCualitativoJuradoMapper() {}

    public static ItemCualitativoJuradoDomain toDomain(ItemCualitativoJuradoEntity entity) {
        return ItemCualitativoJuradoDomain.reconstruir(
                entity.id(), entity.nombre(), entity.descripcion());
    }

    public static ItemCualitativoJuradoEntity toEntity(ItemCualitativoJuradoDomain domain) {
        return new ItemCualitativoJuradoEntity(
                domain.getId(), domain.getNombre(), domain.getDescripcion());
    }
}
