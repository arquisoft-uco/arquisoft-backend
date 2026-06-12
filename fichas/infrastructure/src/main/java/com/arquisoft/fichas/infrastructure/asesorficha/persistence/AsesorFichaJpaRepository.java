package com.arquisoft.fichas.infrastructure.asesorficha.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AsesorFichaJpaRepository extends JpaRepository<AsesorFichaJpaEntity, UUID> {
}
