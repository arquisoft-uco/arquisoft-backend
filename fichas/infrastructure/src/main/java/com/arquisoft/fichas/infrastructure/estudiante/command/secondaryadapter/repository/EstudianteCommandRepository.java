package com.arquisoft.fichas.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstudianteCommandRepository extends JpaRepository<EstudianteEntity, UUID> {
}
