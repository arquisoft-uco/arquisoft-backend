package com.arquisoft.evaluaciones.infrastructure.criterioitemcualitativojurado.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface CriterioItemCualitativoJuradoQueryRepository
        extends QueryRepository<CriterioItemCualitativoJuradoJpaQueryEntity, UUID> {

    List<CriterioItemCualitativoJuradoJpaQueryEntity> findAllByOrderByNombreAsc();
}
