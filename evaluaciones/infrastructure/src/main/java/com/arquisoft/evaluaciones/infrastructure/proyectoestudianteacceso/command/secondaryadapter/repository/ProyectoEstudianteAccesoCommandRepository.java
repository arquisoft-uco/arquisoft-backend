package com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.entity.ProyectoEstudianteAccesoId;
import com.arquisoft.evaluaciones.infrastructure.proyectoestudianteacceso.command.secondaryadapter.entity.ProyectoEstudianteAccesoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ProyectoEstudianteAccesoCommandRepository
        extends JpaRepository<ProyectoEstudianteAccesoJpaEntity, ProyectoEstudianteAccesoId> {

    Optional<ProyectoEstudianteAccesoJpaEntity> findByProyectoAndEstudiante(UUID proyecto, UUID estudiante);
}
