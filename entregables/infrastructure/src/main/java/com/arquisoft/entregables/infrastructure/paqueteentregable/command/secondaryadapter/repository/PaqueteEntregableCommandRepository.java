package com.arquisoft.entregables.infrastructure.paqueteentregable.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.paqueteentregable.command.secondaryadapter.entity.PaqueteEntregableJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PaqueteEntregableCommandRepository extends JpaRepository<PaqueteEntregableJpaEntity, UUID> {

    boolean existsByEntregableProyectoGradoId(UUID entregableProyectoGradoId);
}
