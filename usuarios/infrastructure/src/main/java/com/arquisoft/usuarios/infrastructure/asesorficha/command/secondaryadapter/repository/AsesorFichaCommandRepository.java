package com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.usuarios.infrastructure.asesorficha.command.secondaryadapter.entity.AsesorFichaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface AsesorFichaCommandRepository extends JpaRepository<AsesorFichaJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AsesorFichaJpaEntity a SET a.eliminadoEn = :eliminadoEn WHERE a.usuarioId = :usuario")
    int eliminarLogica(@Param("usuario") UUID usuario, @Param("eliminadoEn") Instant eliminadoEn);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AsesorFichaJpaEntity a SET a.eliminadoEn = NULL WHERE a.usuarioId = :usuario")
    int reactivar(@Param("usuario") UUID usuario);
}
