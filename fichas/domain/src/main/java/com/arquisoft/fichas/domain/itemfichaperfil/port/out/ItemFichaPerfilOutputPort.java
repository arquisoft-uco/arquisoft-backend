package com.arquisoft.fichas.domain.itemfichaperfil.port.out;

import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilAggregate;

import java.util.Optional;
import java.util.UUID;

public interface ItemFichaPerfilOutputPort {

    void guardar(ItemFichaPerfilAggregate item);

    boolean existePorFichaYTipoItem(UUID fichaPerfilId, String tipoItem);

    boolean existePorId(UUID itemId);

    Optional<ItemFichaPerfilAggregate> buscarPorId(UUID itemId);

    void eliminarPorId(UUID itemId);
}
