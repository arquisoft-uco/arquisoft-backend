package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EntregableProyectoGradoCommandRepository
        extends JpaRepository<EntregableProyectoGradoJpaEntity, UUID> {

    @Query("SELECT COALESCE(MAX(e.version), 0) FROM EntregableProyectoGradoJpaEntity e "
            + "WHERE e.proyectoGrado = :proyectoGrado")
    int obtenerUltimaVersion(@Param("proyectoGrado") UUID proyectoGrado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EntregableProyectoGradoJpaEntity e WHERE e.id = :id")
    Optional<EntregableProyectoGradoJpaEntity> buscarBloqueadaPorId(@Param("id") UUID id);

    @Query("""
            SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END
            FROM EntregableProyectoGradoJpaEntity e
            WHERE e.proyectoGrado = :proyectoGrado
              AND EXISTS (
                  SELECT 1 FROM EstadoEntregableProyectoGradoJpaEntity s
                  WHERE s.entregableProyectoGrado = e.id
                    AND s.estadoEntregable <> :estadoExcluido
                    AND NOT EXISTS (
                        SELECT 1 FROM EstadoEntregableProyectoGradoJpaEntity p
                        WHERE p.entregableProyectoGrado = s.entregableProyectoGrado
                          AND p.fechaActualizacion > s.fechaActualizacion))
            """)
    boolean existeVigentePorProyectoGrado(@Param("proyectoGrado") UUID proyectoGrado,
                                          @Param("estadoExcluido") String estadoExcluido);
}
