package com.arquisoft.evaluaciones.infrastructure.itemcuantitativoasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface ItemCuantitativoAsesorQueryRepository
        extends QueryRepository<ItemCuantitativoAsesorJpaQueryEntity, UUID> {

    List<ItemCuantitativoAsesorJpaQueryEntity> findAllByOrderByCategoriaIdAscNombreAsc();
}
