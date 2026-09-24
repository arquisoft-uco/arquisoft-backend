package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.mapper;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.entity.ItemCuantitativoJuradoEntity;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;

public final class ItemCuantitativoJuradoMapper {

    private ItemCuantitativoJuradoMapper() {}

    public static ItemCuantitativoJuradoDomain toDomain(
            ItemCuantitativoJuradoEntity entity) {
        return ItemCuantitativoJuradoDomain.reconstruir(
                entity.id(),
                entity.nombre(),
                entity.descripcion(),
                entity.categoriaId(),
                entity.valor());
    }

    public static ItemCuantitativoJuradoEntity toEntity(
            ItemCuantitativoJuradoDomain domain) {
        return new ItemCuantitativoJuradoEntity(
                domain.getId(),
                domain.getNombre(),
                domain.getDescripcion(),
                domain.getCategoria(),
                domain.getValor());
    }
}
