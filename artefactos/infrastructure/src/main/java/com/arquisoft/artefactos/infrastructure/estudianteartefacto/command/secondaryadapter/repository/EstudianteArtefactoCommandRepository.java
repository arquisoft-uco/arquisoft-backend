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
            WHERE ea.artefacto = :artefacto
              AND ea.estudiante = :estudiante
            """)
    int eliminarPorArtefactoYEstudiante(@Param("artefacto") UUID artefacto, @Param("estudiante") UUID estudiante);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            DELETE FROM EstudianteArtefactoJpaEntity ea
            WHERE ea.estudiante = :estudiante
              AND ea.artefacto IN (SELECT a.id FROM ArtefactoJpaEntity a WHERE a.proyectoGrado = :proyectoGrado)
            """)
    int eliminarPorProyectoGradoYEstudiante(
            @Param("proyectoGrado") UUID proyectoGrado, @Param("estudiante") UUID estudiante);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            DELETE FROM EstudianteArtefactoJpaEntity ea
            WHERE ea.artefacto = :artefacto
              AND (ea.estudiante NOT IN :conservar
                   OR NOT EXISTS (
                       SELECT 1
                       FROM ArtefactoJpaEntity a, EstudianteProyectoGradoJpaEntity epg
                       WHERE a.id = ea.artefacto
                         AND epg.proyectoGradoId = a.proyectoGrado
                         AND epg.estudianteId = ea.estudiante
                         AND epg.destituidoEn IS NULL))
            """)
    int eliminarExceptoConservados(@Param("artefacto") UUID artefacto, @Param("conservar") List<UUID> conservar);

    @Query("""
            SELECT ea.estudiante
            FROM EstudianteArtefactoJpaEntity ea, ArtefactoJpaEntity a, EstudianteProyectoGradoJpaEntity epg
            WHERE ea.artefacto = :artefacto
              AND a.id = ea.artefacto
              AND epg.proyectoGradoId = a.proyectoGrado
              AND epg.estudianteId = ea.estudiante
              AND epg.destituidoEn IS NULL
            """)
    List<UUID> buscarVigentesVinculados(@Param("artefacto") UUID artefacto);
}
