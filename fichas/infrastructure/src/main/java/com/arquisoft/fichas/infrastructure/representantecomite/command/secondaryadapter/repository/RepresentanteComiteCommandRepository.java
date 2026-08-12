package com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.representantecomite.command.secondaryport.entity.RepresentanteComiteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RepresentanteComiteCommandRepository
        extends JpaRepository<RepresentanteComiteEntity, UUID> {
}
