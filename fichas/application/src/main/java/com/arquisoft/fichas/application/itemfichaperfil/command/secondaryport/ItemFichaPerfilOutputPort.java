package com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport;

import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity.ItemFichaPerfilEntity;
import com.arquisoft.fichas.application.itemfichaperfil.command.secondaryport.entity.PertenenciaItemFichaPerfilEntity;

import java.util.Optional;
import java.util.UUID;

public interface ItemFichaPerfilOutputPort {

    void registrarItem(ItemFichaPerfilEntity item);

    void actualizarContenido(UUID item, String contenido);

    boolean existePorFichaYTipoItem(UUID fichaPerfilId, String tipoItem);

    boolean existePorId(UUID itemId);

    Optional<UUID> obtenerFichaPerfilId(UUID itemId);

    Optional<PertenenciaItemFichaPerfilEntity> obtenerPertenencia(UUID item, UUID estudiante);

    void removerItem(UUID itemId);
}
