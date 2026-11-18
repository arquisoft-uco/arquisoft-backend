package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.entity.EntregableProyectoGradoJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EntregableProyectoGradoCommandRepository
        extends JpaRepository<EntregableProyectoGradoJpaEntity, UUID> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM EntregableProyectoGradoJpaEntity e WHERE e.id = :id")
    Optional<EntregableProyectoGradoJpaEntity> buscarBloqueadoPorId(@Param("id") UUID id);
}
