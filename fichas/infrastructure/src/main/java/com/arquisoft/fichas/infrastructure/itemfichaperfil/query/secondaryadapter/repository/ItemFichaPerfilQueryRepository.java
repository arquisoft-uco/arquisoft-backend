package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ItemFichaPerfilQueryRepository
        extends QueryRepository<ItemFichaPerfilJpaQueryEntity, UUID> {

    List<ItemFichaPerfilJpaQueryEntity> findByFichaPerfilIdAndAsesorIdOrderByTipoItemNombreAsc(
            UUID fichaPerfilId, UUID asesorId);
}
