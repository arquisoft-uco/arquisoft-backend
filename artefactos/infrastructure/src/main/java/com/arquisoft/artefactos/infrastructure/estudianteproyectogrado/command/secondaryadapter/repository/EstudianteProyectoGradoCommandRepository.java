package com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstudianteProyectoGradoCommandRepository
        extends JpaRepository<EstudianteProyectoGradoJpaEntity, UUID> {

    List<EstudianteProyectoGradoJpaEntity> findByProyectoGradoIdAndEstudianteIdIn(
            UUID proyectoGradoId, List<UUID> estudiantesIds);

    Optional<EstudianteProyectoGradoJpaEntity> findByEstudianteIdAndProyectoGradoId(
            UUID estudianteId, UUID proyectoGradoId);

    @Query("""
            SELECT epg.estudianteId
            FROM ArtefactoJpaEntity a, EstudianteProyectoGradoJpaEntity epg
            WHERE a.id = :artefacto
              AND epg.proyectoGradoId = a.proyectoGrado
              AND epg.estudianteId IN :estudiantes
              AND epg.destituidoEn IS NULL
            """)
    List<UUID> buscarEstudiantesVigentesDelArtefacto(
            @Param("artefacto") UUID artefacto, @Param("estudiantes") List<UUID> estudiantes);

    @Query("""
            SELECT epg.estudianteId
            FROM EstudianteProyectoGradoJpaEntity epg
            WHERE epg.proyectoGradoId = :proyectoGrado
              AND epg.destituidoEn IS NULL
            """)
    List<UUID> buscarVigentesPorProyectoGrado(@Param("proyectoGrado") UUID proyectoGrado);
}
