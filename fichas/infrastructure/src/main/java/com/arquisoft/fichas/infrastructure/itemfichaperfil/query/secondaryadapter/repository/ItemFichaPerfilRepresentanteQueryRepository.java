package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ItemFichaPerfilRepresentanteQueryRepository
        extends QueryRepository<ItemFichaPerfilRepresentanteJpaQueryEntity, UUID> {

    List<ItemFichaPerfilRepresentanteJpaQueryEntity> findByFichaPerfilIdAndRepresentanteComiteIdOrderByTipoItemNombreAsc(
            UUID fichaPerfilId, UUID representanteComiteId);
}
