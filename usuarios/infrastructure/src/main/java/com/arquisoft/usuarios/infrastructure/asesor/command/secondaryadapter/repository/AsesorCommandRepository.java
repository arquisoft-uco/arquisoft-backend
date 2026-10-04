package com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.repository;

import com.arquisoft.usuarios.infrastructure.asesor.command.secondaryadapter.entity.AsesorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface AsesorCommandRepository extends JpaRepository<AsesorJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AsesorJpaEntity a SET a.eliminadoEn = :eliminadoEn WHERE a.usuarioId = :usuario")
    int eliminarLogica(@Param("usuario") UUID usuario, @Param("eliminadoEn") Instant eliminadoEn);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AsesorJpaEntity a SET a.eliminadoEn = NULL WHERE a.usuarioId = :usuario")
    int reactivar(@Param("usuario") UUID usuario);
}
