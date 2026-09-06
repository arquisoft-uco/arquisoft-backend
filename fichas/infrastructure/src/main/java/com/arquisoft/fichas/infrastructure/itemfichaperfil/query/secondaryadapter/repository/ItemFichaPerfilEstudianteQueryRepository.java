package com.arquisoft.fichas.infrastructure.itemfichaperfil.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ItemFichaPerfilEstudianteQueryRepository
        extends QueryRepository<ItemFichaPerfilEstudianteJpaQueryEntity, UUID> {

    List<ItemFichaPerfilEstudianteJpaQueryEntity> findByFichaPerfilIdAndEstudianteIdOrderByTipoItemNombreAsc(
            UUID fichaPerfilId, UUID estudianteId);
}
