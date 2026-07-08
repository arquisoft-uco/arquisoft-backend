package com.arquisoft.fichas.infrastructure.representantecomite.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepresentanteComiteJpaRepository
        extends JpaRepository<RepresentanteComiteJpaEntity, UUID> {
}
