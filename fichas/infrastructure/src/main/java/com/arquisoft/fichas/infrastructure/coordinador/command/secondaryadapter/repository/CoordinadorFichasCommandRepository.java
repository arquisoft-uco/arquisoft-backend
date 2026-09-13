package com.arquisoft.fichas.infrastructure.coordinador.command.secondaryadapter.repository;

import com.arquisoft.fichas.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoordinadorFichasCommandRepository extends JpaRepository<CoordinadorJpaEntity, UUID> {
}
