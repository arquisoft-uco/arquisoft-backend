package com.arquisoft.proyectos.infrastructure.coordinador.command.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CoordinadorProyectosCommandRepository extends JpaRepository<CoordinadorJpaEntity, UUID> {
}
