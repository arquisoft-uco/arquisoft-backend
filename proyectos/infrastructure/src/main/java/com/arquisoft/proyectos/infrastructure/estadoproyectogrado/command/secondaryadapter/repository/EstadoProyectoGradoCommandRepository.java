package com.arquisoft.proyectos.infrastructure.estadoproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.estadoproyectogrado.command.secondaryadapter.entity.EstadoProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EstadoProyectoGradoCommandRepository extends JpaRepository<EstadoProyectoGradoJpaEntity, UUID> {

    Optional<EstadoProyectoGradoJpaEntity> findFirstByProyectoGradoIdOrderByFechaActualizacionDesc(
            UUID proyectoGradoId);
}
