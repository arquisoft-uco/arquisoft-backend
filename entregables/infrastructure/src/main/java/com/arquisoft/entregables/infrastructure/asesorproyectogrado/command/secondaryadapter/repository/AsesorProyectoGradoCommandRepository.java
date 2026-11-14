package com.arquisoft.entregables.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.asesorproyectogrado.command.secondaryadapter.entity.AsesorProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface AsesorProyectoGradoCommandRepository extends JpaRepository<AsesorProyectoGradoJpaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT a FROM AsesorProyectoGradoJpaEntity a
            WHERE a.asesorId = :asesorId AND a.proyectoGradoId = :proyectoGradoId
            """)
    Optional<AsesorProyectoGradoJpaEntity> buscarBloqueadoPorAsesorYProyectoGrado(
            @Param("asesorId") UUID asesorId, @Param("proyectoGradoId") UUID proyectoGradoId);

    @Query("""
            SELECT CASE WHEN a.finalizadoEn IS NULL THEN a.asesorId END
            FROM AsesorProyectoGradoJpaEntity a
            WHERE a.proyectoGradoId = :proyectoGradoId
            ORDER BY a.ocurridoEn DESC, a.asesorId
            LIMIT 1
            """)
    Optional<UUID> buscarAsesorVigentePorProyectoGrado(@Param("proyectoGradoId") UUID proyectoGradoId);

    @Query("""
            SELECT CASE WHEN a.finalizadoEn IS NULL THEN a.asesorId END
            FROM AsesorProyectoGradoJpaEntity a
            WHERE a.proyectoGradoId = (SELECT e.proyectoGrado FROM EntregableProyectoGradoJpaEntity e
                                       WHERE e.id = :entregableId)
            ORDER BY a.ocurridoEn DESC, a.asesorId
            LIMIT 1
            """)
    Optional<UUID> buscarAsesorVigentePorEntregable(@Param("entregableId") UUID entregableId);
}
