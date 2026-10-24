package com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.artefacto.command.secondaryadapter.entity.ArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtefactoCommandRepository extends JpaRepository<ArtefactoJpaEntity, UUID> {
}
