package com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstudianteCommandRepository extends JpaRepository<EstudianteJpaEntity, UUID> {
}
