package com.arquisoft.evaluaciones.infrastructure.itemcuantitativojurado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ItemCuantitativoJuradoQueryRepository
        extends QueryRepository<ItemCuantitativoJuradoJpaQueryEntity, UUID> {

    List<ItemCuantitativoJuradoJpaQueryEntity> findAllByOrderByCategoriaIdAscNombreAsc();
}
