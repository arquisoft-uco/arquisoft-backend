package com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.repository;

import com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.entity.CoordinadorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface CoordinadorCommandRepository extends JpaRepository<CoordinadorJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE CoordinadorJpaEntity c SET c.eliminadoEn = :eliminadoEn WHERE c.usuarioId = :usuario")
    int eliminarLogica(@Param("usuario") UUID usuario, @Param("eliminadoEn") Instant eliminadoEn);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE CoordinadorJpaEntity c SET c.eliminadoEn = NULL WHERE c.usuarioId = :usuario")
    int reactivar(@Param("usuario") UUID usuario);
}
