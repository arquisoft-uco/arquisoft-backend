package com.arquisoft.proyectos.infrastructure.estadoasesor.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;

public interface EstadoAsesorQueryRepository extends QueryRepository<EstadoAsesorJpaQueryEntity, String> {

    List<EstadoAsesorJpaQueryEntity> findAllByOrderByIdAsc();
}
