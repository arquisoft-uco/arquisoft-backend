package com.arquisoft.evaluaciones.infrastructure.coordinadorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.infrastructure.coordinadorproyectogrado.command.secondaryadapter.entity.CoordinadorProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface CoordinadorProyectoGradoCommandRepository
        extends JpaRepository<CoordinadorProyectoGradoJpaEntity, UUID> {

    @NativeQuery("""
            SELECT c.*
            FROM evaluacion e
            JOIN entregable en ON en.id = e.entregable_id
            JOIN coordinador_proyecto_grado c ON c.proyecto_grado_id = en.proyecto_grado_id
            WHERE e.id = :evaluacion
            """)
    Optional<CoordinadorProyectoGradoJpaEntity> findPorEvaluacion(@Param("evaluacion") UUID evaluacion);
}
