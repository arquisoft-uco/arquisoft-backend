package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EstudianteProyectoGradoCommandRepository extends JpaRepository<EstudianteProyectoGradoJpaEntity, UUID> {

    long countByProyectoGradoId(UUID proyectoGradoId);
}
