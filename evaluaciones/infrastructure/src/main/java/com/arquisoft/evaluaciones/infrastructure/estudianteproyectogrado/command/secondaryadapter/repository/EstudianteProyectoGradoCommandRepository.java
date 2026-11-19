package com.arquisoft.evaluaciones.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.NativeQuery;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstudianteProyectoGradoCommandRepository
        extends JpaRepository<EstudianteProyectoGradoJpaEntity, UUID> {

    List<EstudianteProyectoGradoJpaEntity> findByProyectoGradoId(UUID proyectoGradoId);

    Optional<EstudianteProyectoGradoJpaEntity> findByEstudianteIdAndProyectoGradoId(
            UUID estudianteId, UUID proyectoGradoId);

    @NativeQuery("""
            SELECT epg.*
            FROM evaluacion e
            JOIN entregable en ON en.id = e.entregable_id
            JOIN estudiante_proyecto_grado epg ON epg.proyecto_grado_id = en.proyecto_grado_id
            WHERE e.id = :evaluacion
              AND epg.eliminado_en IS NULL
            """)
    List<EstudianteProyectoGradoJpaEntity> findContactosVigentesPorEvaluacion(@Param("evaluacion") UUID evaluacion);
}
