package com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.estudianteartefacto.command.secondaryadapter.entity.EstudianteArtefactoJpaEntity;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface EstudianteArtefactoCommandRepository extends JpaRepository<EstudianteArtefactoJpaEntity, UUID> {

    boolean existsByArtefactoAndEstudiante(UUID artefacto, UUID estudiante);

    List<EstudianteArtefactoJpaEntity> findByArtefactoAndEstudianteIn(UUID artefacto, List<UUID> estudiantes);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            DELETE FROM EstudianteArtefactoJpaEntity ea
            WHERE ea.estudiante = :estudiante
              AND ea.artefacto IN (SELECT a.id FROM ArtefactoJpaEntity a WHERE a.proyectoGrado = :proyectoGrado)
            """)
    int eliminarPorProyectoGradoYEstudiante(
            @Param("proyectoGrado") UUID proyectoGrado, @Param("estudiante") UUID estudiante);
}
