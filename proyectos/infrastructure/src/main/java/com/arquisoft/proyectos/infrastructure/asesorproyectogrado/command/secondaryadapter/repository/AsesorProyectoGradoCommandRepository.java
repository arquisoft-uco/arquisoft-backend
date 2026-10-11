package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoCommandRepository extends JpaRepository<AsesorProyectoGradoJpaEntity, UUID> {

    @Query("""
            SELECT COUNT(a) > 0 FROM AsesorProyectoGradoJpaEntity a
            WHERE a.asesorId = :asesorId
              AND a.proyectoGradoId = :proyectoGradoId
              AND a.estadoAsesorId = :estadoAsesorId
              AND NOT EXISTS (SELECT 1 FROM AsesorProyectoGradoJpaEntity s
                              WHERE s.asesorId = a.asesorId
                                AND s.proyectoGradoId = a.proyectoGradoId
                                AND s.fechaAsignado > a.fechaAsignado)
            """)
    boolean existsByAsesorAndProyectoGradoWithEstadoVigente(
            @Param("asesorId") UUID asesorId,
            @Param("proyectoGradoId") UUID proyectoGradoId,
            @Param("estadoAsesorId") String estadoAsesorId);

    @Query("""
            SELECT COUNT(a) FROM AsesorProyectoGradoJpaEntity a
            WHERE a.proyectoGradoId = :proyectoGradoId
              AND a.estadoAsesorId = :estadoAsesorId
              AND NOT EXISTS (SELECT 1 FROM AsesorProyectoGradoJpaEntity s
                              WHERE s.asesorId = a.asesorId
                                AND s.proyectoGradoId = a.proyectoGradoId
                                AND s.fechaAsignado > a.fechaAsignado)
            """)
    long countByProyectoGradoWithEstadoVigente(
            @Param("proyectoGradoId") UUID proyectoGradoId,
            @Param("estadoAsesorId") String estadoAsesorId);

    Optional<AsesorProyectoGradoJpaEntity> findFirstByAsesorIdAndProyectoGradoIdOrderByFechaAsignadoDesc(
            UUID asesorId, UUID proyectoGradoId);
}
