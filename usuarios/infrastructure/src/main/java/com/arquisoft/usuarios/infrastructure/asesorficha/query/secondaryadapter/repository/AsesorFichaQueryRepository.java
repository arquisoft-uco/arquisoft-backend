package com.arquisoft.usuarios.infrastructure.asesorficha.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface AsesorFichaQueryRepository
        extends SpecificationQueryRepository<AsesorFichaJpaQueryEntity, UUID> {
}
