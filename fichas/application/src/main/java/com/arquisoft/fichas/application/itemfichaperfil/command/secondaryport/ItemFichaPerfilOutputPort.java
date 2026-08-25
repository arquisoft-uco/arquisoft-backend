package com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport;

import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity.ItemFichaPerfilEntity;

import java.util.Optional;
import java.util.UUID;

public interface ItemFichaPerfilOutputPort {

    void registrarItem(ItemFichaPerfilEntity item);

    void actualizarContenido(UUID item, String contenido);

    boolean existePorFichaYTipoItem(UUID fichaPerfilId, String tipoItem);

    boolean existePorId(UUID itemId);

    Optional<UUID> obtenerFichaPerfilId(UUID itemId);

    void removerItem(UUID itemId);
}
