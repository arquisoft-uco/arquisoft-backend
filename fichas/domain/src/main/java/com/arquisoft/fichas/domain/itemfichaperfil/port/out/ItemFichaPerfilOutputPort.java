package com.arquisoft.fichas.domain.itemfichaperfil.port.out;

import com.arquisoft.fichas.domain.itemfichaperfil.aggregate.ItemFichaPerfilDomain;

import java.util.Optional;
import java.util.UUID;

public interface ItemFichaPerfilOutputPort {

    void registrarItem(ItemFichaPerfilDomain item);

    void actualizarContenido(UUID item, String contenido);

    boolean existePorFichaYTipoItem(UUID fichaPerfilId, String tipoItem);

    boolean existePorId(UUID itemId);

    Optional<UUID> obtenerFichaPerfilId(UUID itemId);

    void removerItem(UUID itemId);
}
