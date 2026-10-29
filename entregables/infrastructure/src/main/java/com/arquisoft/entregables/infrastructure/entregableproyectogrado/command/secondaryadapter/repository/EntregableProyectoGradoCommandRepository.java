package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying(clearAutomatically = true)
    @Query("UPDATE EntregableProyectoGradoJpaEntity e SET e.estadoEntregable = :estado WHERE e.id = :id")
    void actualizarEstado(@Param("id") UUID id, @Param("estado") String estado);
}
