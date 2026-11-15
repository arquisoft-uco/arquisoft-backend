package com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.entity.DocumentacionJpaEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface DocumentacionCommandRepository extends JpaRepository<DocumentacionJpaEntity, UUID> {

    // El estado leido es precondicion del cambio: dos bibliotecarios no deben decidir sobre el mismo estado previo.
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM DocumentacionJpaEntity d WHERE d.id = :documentacion")
    Optional<DocumentacionJpaEntity> buscarBloqueadaPorId(@Param("documentacion") UUID documentacion);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE DocumentacionJpaEntity d SET d.estadoDocumentacion = :estado WHERE d.id = :documentacion")
    int actualizarEstado(@Param("documentacion") UUID documentacion, @Param("estado") String estado);
}
