package com.arquisoft.usuarios.infrastructure.bibliotecario.query.secondaryadapter.repository;

import com.arquisoft.shared.jpa.repository.SpecificationQueryRepository;

import java.util.UUID;

public interface BibliotecarioQueryRepository
        extends SpecificationQueryRepository<BibliotecarioJpaQueryEntity, UUID> {
}
