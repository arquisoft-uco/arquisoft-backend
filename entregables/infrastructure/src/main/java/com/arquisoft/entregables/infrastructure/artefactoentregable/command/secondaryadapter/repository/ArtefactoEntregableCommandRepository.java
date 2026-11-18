package com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.artefactoentregable.command.secondaryadapter.entity.ArtefactoEntregableJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ArtefactoEntregableCommandRepository extends JpaRepository<ArtefactoEntregableJpaEntity, UUID> {
}
