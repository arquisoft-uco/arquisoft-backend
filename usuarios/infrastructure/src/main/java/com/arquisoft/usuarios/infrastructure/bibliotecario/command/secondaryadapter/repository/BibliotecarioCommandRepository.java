package com.arquisoft.usuarios.infrastructure.bibliotecario.command.secondaryadapter.repository;

import com.arquisoft.usuarios.infrastructure.bibliotecario.command.secondaryadapter.entity.BibliotecarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface BibliotecarioCommandRepository extends JpaRepository<BibliotecarioJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE BibliotecarioJpaEntity b SET b.eliminadoEn = :eliminadoEn WHERE b.usuarioId = :usuario")
    int eliminarLogica(@Param("usuario") UUID usuario, @Param("eliminadoEn") Instant eliminadoEn);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE BibliotecarioJpaEntity b SET b.eliminadoEn = NULL WHERE b.usuarioId = :usuario")
    int reactivar(@Param("usuario") UUID usuario);
}
