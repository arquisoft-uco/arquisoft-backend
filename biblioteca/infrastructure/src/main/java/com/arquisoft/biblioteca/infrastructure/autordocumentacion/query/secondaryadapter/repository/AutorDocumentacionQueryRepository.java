package com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.List;
import java.util.UUID;

public interface AutorDocumentacionQueryRepository
        extends QueryRepository<AutorDocumentacionJpaQueryEntity, UUID> {

    List<AutorDocumentacionJpaQueryEntity> findByDocumentacionIdOrderByNombreAscIdAsc(UUID documentacionId);
}
