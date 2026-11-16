package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.QueryRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface AutorDeDocumentacionQueryRepository
        extends QueryRepository<AutorDeDocumentacionJpaQueryEntity, UUID> {

    List<AutorDeDocumentacionJpaQueryEntity> findByDocumentacionIdInOrderByNombreAscIdAsc(
            Collection<UUID> documentaciones);
}
