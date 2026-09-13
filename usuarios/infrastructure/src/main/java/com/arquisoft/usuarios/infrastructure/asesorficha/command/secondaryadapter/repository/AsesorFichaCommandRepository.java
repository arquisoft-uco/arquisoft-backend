package com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AsesorFichaCommandRepository extends JpaRepository<AsesorFichaJpaEntity, UUID> {
}
