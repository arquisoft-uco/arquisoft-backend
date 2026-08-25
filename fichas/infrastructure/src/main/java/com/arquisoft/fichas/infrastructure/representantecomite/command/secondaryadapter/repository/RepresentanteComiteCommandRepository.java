package com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.entity.RepresentanteComiteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepresentanteComiteCommandRepository
        extends JpaRepository<RepresentanteComiteJpaEntity, UUID> {
}
