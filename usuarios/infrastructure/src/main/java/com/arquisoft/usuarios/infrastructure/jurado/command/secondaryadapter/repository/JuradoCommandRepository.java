package com.arquisoft.usuarios.infrastructure.jurado.command.secondaryadapter.repository;

import com.arquisoft.usuarios.infrastructure.jurado.command.secondaryadapter.entity.JuradoJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface JuradoCommandRepository extends JpaRepository<JuradoJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE JuradoJpaEntity j SET j.eliminadoEn = :eliminadoEn WHERE j.usuarioId = :usuario")
    int eliminarLogica(@Param("usuario") UUID usuario, @Param("eliminadoEn") Instant eliminadoEn);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE JuradoJpaEntity j SET j.eliminadoEn = NULL WHERE j.usuarioId = :usuario")
    int reactivar(@Param("usuario") UUID usuario);
}
