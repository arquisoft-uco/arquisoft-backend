package com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.estudianteproyectogrado.command.secondaryadapter.entity.EstudianteProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EstudianteProyectoGradoCommandRepository
        extends JpaRepository<EstudianteProyectoGradoJpaEntity, UUID> {

    List<EstudianteProyectoGradoJpaEntity> findByProyectoGradoId(UUID proyectoGradoId);

    Optional<EstudianteProyectoGradoJpaEntity> findByEstudianteIdAndProyectoGradoId(
            UUID estudianteId, UUID proyectoGradoId);

    @NativeQuery("""
            SELECT e.*
            FROM estudiante_proyecto_grado e
            WHERE e.proyecto_grado_id = :proyectoGrado
              AND e.eliminado_en IS NULL
              AND e.email IS NOT NULL
              AND TRIM(e.email) <> ''
            """)
    List<EstudianteProyectoGradoJpaEntity> buscarContactosVigentesPorProyectoGrado(
            @Param("proyectoGrado") UUID proyectoGrado);

    @NativeQuery("""
            SELECT e.*
            FROM estudiante_proyecto_grado e
            JOIN entregable_proyecto_grado g ON g.proyecto_grado_id = e.proyecto_grado_id
            WHERE g.id = :entregableProyectoGrado
              AND e.eliminado_en IS NULL
              AND e.email IS NOT NULL
              AND TRIM(e.email) <> ''
            """)
    List<EstudianteProyectoGradoJpaEntity> buscarContactosVigentesPorEntregable(
            @Param("entregableProyectoGrado") UUID entregableProyectoGrado);
}
