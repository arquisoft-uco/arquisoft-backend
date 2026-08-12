package com.arquisoft.fichas.infrastructure.estudiante.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstudianteQueryRepository extends JpaRepository<EstudianteEntity, UUID> {
}
