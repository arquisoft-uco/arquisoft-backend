package com.arquisoft.fichas.domain.itemfichaperfil.port.out;

import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilDomain;

import java.util.Optional;
import java.util.UUID;

public interface ItemFichaPerfilOutputPort {

    void guardar(ItemFichaPerfilDomain item);

    boolean existePorFichaYTipoItem(UUID fichaPerfilId, String tipoItem);

    boolean existePorId(UUID itemId);

    Optional<ItemFichaPerfilDomain> buscarPorId(UUID itemId);

    void eliminarPorId(UUID itemId);
}
