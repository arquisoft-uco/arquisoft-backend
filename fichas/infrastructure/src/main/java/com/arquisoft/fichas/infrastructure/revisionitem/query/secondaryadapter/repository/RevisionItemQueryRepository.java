package com.arquisoft.fichas.infrastructure.revisionitem.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface RevisionItemQueryRepository
        extends SpecificationQueryRepository<RevisionItemJpaQueryEntity, UUID> {
}
