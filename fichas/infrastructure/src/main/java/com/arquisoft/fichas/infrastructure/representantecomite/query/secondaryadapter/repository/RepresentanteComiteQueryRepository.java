package com.arquisoft.fichas.infrastructure.representantecomite.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepresentanteComiteQueryRepository
        extends JpaRepository<RepresentanteComiteEntity, UUID> {
}
