package com.arquisoft.evaluaciones.infrastructure.itemcualitativoasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ItemCualitativoAsesorQueryRepository
        extends QueryRepository<ItemCualitativoAsesorJpaQueryEntity, UUID> {

    List<ItemCualitativoAsesorJpaQueryEntity> findAllByOrderByNombreAsc();
}
