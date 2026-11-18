package com.arquisoft.evaluaciones.infrastructure.entregable.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.entregable.command.secondaryadapter.entity.EntregableJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EntregableCommandRepository extends JpaRepository<EntregableJpaEntity, UUID> {
}
