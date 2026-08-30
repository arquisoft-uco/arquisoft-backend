package com.arquisoft.evaluaciones.infrastructure.itemcualitativojurado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ItemCualitativoJuradoQueryRepository
        extends QueryRepository<ItemCualitativoJuradoJpaQueryEntity, UUID> {

    List<ItemCualitativoJuradoJpaQueryEntity> findAllByOrderByNombreAsc();
}
