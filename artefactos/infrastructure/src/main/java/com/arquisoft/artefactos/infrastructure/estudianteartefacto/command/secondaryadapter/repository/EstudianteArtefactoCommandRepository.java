package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.entity.EstudianteArtefactoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EstudianteArtefactoCommandRepository extends JpaRepository<EstudianteArtefactoJpaEntity, UUID> {

    boolean existsByArtefactoAndEstudiante(UUID artefacto, UUID estudiante);

    List<EstudianteArtefactoJpaEntity> findByArtefactoInAndEstudianteIn(
            List<UUID> artefactos, List<UUID> estudiantes);
}
