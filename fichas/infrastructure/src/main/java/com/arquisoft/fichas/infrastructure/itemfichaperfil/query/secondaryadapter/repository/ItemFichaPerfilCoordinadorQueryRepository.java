package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ItemFichaPerfilCoordinadorQueryRepository
        extends QueryRepository<ItemFichaPerfilCoordinadorJpaQueryEntity, UUID> {

    List<ItemFichaPerfilCoordinadorJpaQueryEntity> findByFichaPerfilIdOrderByTipoItemNombreAsc(UUID fichaPerfilId);
}
