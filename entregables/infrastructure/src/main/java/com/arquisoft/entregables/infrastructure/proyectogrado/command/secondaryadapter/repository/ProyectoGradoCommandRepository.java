package com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.proyectogrado.command.secondaryadapter.entity.ProyectoGradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.util.Optional;
import java.util.UUID;

public interface ProyectoGradoCommandRepository extends JpaRepository<ProyectoGradoJpaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM ProyectoGradoJpaEntity p WHERE p.id = :id")
    Optional<ProyectoGradoJpaEntity> buscarBloqueadoPorId(@Param("id") UUID id);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
            SELECT p FROM ProyectoGradoJpaEntity p
            WHERE p.id = (SELECT e.proyectoGrado FROM EntregableProyectoGradoJpaEntity e WHERE e.id = :entregable)
            """)
    Optional<ProyectoGradoJpaEntity> buscarBloqueadoPorEntregable(@Param("entregable") UUID entregable);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE ProyectoGradoJpaEntity p SET p.estadoProyectoId = :estado WHERE p.id = :id")
    int actualizarEstado(@Param("id") UUID id, @Param("estado") String estado);
}
