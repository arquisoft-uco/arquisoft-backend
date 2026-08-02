package com.arquisoft.fichas.infrastructure.estudiante.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstudianteRepository extends JpaRepository<EstudianteEntity, UUID> {
}
