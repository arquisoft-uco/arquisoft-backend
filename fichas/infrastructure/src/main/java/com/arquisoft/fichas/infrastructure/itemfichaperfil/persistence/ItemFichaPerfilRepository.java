package com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemFichaPerfilRepository extends JpaRepository<ItemFichaPerfilEntity, UUID> {

    boolean existsByFichaPerfilIdAndTipoItemId(UUID fichaPerfilId, String tipoItemId);
}
