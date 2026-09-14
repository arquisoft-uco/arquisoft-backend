package com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport;

import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.secondaryport.entity.ItemCuantitativoJuradoEntity;

import java.util.Optional;
import java.util.UUID;

public interface ItemCuantitativoJuradoOutputPort {

    void registrar(ItemCuantitativoJuradoEntity item);

    boolean existeCategoriaPorId(UUID categoriaId);

    boolean existePorNombreYCategoriaIgnorandoMayusculas(
            String nombre, UUID categoriaId);

    Optional<ItemCuantitativoJuradoEntity> obtenerPorId(UUID id);
}
