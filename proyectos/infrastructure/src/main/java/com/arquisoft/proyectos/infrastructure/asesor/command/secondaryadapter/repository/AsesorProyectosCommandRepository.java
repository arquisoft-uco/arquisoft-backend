package com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AsesorProyectosCommandRepository extends JpaRepository<AsesorJpaEntity, UUID> {
}
