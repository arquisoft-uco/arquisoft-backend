package com.arquisoft.solicitudes.infrastructure.solicitud.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface SolicitudQueryRepository
        extends SpecificationQueryRepository<SolicitudJpaQueryEntity, UUID> {
}
