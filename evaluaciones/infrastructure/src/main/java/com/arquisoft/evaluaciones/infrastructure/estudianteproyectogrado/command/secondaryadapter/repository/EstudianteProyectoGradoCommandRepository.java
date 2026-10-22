package com.arquisoft.evaluaciones.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EstudianteProyectoGradoCommandRepository
        extends JpaRepository<EstudianteProyectoGradoJpaEntity, UUID> {

    List<EstudianteProyectoGradoJpaEntity> findByProyectoGradoId(UUID proyectoGradoId);
}
