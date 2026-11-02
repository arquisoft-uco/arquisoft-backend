package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativoasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface CriterioItemCualitativoAsesorQueryRepository
        extends QueryRepository<CriterioItemCualitativoAsesorJpaQueryEntity, UUID> {

    List<CriterioItemCualitativoAsesorJpaQueryEntity> findAllByOrderByNombreAsc();
}
