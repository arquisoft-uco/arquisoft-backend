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

    record PertenenciaProyeccion(UUID artefacto, UUID estudiante) {
    }

    @Query("""
            SELECT new com.arquisoft.artefactos.infrastructure.estudianteproyectogrado.command.secondaryadapter.\
            repository.EstudianteProyectoGradoCommandRepository$PertenenciaProyeccion(a.id, epg.estudianteId)
            FROM ArtefactoJpaEntity a, EstudianteProyectoGradoJpaEntity epg
            WHERE epg.proyectoGradoId = a.proyectoGrado
              AND a.id IN :artefactos
              AND epg.estudianteId IN :estudiantes
              AND epg.destituidoEn IS NULL
            """)
    List<PertenenciaProyeccion> buscarPertenenciasVigentes(
            @Param("artefactos") List<UUID> artefactos, @Param("estudiantes") List<UUID> estudiantes);
}
