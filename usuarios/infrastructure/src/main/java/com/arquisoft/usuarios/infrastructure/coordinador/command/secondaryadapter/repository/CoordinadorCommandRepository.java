package com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.repository;

import com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoordinadorCommandRepository extends JpaRepository<CoordinadorJpaEntity, UUID> {
}
