package com.arquisoft.artefactos.infrastructure.estadorevisionasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;

public interface EstadoRevisionAsesorQueryRepository
        extends QueryRepository<EstadoRevisionAsesorJpaQueryEntity, String> {

    List<EstadoRevisionAsesorJpaQueryEntity> findAllByOrderByOrdenAsc();
}
