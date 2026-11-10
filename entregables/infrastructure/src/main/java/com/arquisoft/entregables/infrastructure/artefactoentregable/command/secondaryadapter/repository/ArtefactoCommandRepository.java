package com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.entity.ArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtefactoCommandRepository extends JpaRepository<ArtefactoJpaEntity, UUID> {
}
