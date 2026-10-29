package com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstudianteProyectoGradoCommandRepository
        extends JpaRepository<EstudianteProyectoGradoJpaEntity, UUID> {

    List<EstudianteProyectoGradoJpaEntity> findByProyectoGradoId(UUID proyectoGradoId);

    Optional<EstudianteProyectoGradoJpaEntity> findByEstudianteIdAndProyectoGradoId(
            UUID estudianteId, UUID proyectoGradoId);
}
