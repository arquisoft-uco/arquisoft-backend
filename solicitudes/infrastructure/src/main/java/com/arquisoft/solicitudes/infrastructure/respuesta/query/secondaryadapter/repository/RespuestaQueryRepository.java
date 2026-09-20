package com.arquisoft.solicitudes.infrastructure.respuesta.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface RespuestaQueryRepository
        extends SpecificationQueryRepository<RespuestaJpaQueryEntity, UUID> {
}
