package com.arquisoft.fichas.infrastructure.itemfichaperfil.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemFichaPerfilJpaRepository extends JpaRepository<ItemFichaPerfilJpaEntity, UUID> {

    boolean existsByFichaPerfilIdAndTipoItemId(UUID fichaPerfilId, String tipoItemId);
}
