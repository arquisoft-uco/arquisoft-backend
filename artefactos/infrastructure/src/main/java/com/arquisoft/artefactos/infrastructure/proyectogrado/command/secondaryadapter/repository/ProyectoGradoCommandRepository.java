package com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.artefactos.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoCommandRepository extends JpaRepository<ProyectoGradoJpaEntity, UUID> {

    @NativeQuery("""
            SELECT p.*
            FROM proyecto_grado p
            JOIN artefacto a ON a.proyecto_grado_id = p.id
            WHERE a.id = :artefacto
            """)
    Optional<ProyectoGradoJpaEntity> obtenerPorArtefacto(@Param("artefacto") UUID artefacto);

    @NativeQuery("""
            SELECT p.*
            FROM proyecto_grado p
            JOIN artefacto a ON a.proyecto_grado_id = p.id
            WHERE a.id = :artefacto
            FOR UPDATE OF p
            """)
    Optional<ProyectoGradoJpaEntity> bloquearPorArtefacto(@Param("artefacto") UUID artefacto);
}
