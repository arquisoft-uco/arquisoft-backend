package com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.entity.EstudianteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.UUID;

public interface EstudianteCommandRepository extends JpaRepository<EstudianteJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE EstudianteJpaEntity e SET e.eliminadoEn = :eliminadoEn WHERE e.usuarioId = :usuario")
    int eliminarLogica(@Param("usuario") UUID usuario, @Param("eliminadoEn") Instant eliminadoEn);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE EstudianteJpaEntity e SET e.eliminadoEn = NULL WHERE e.usuarioId = :usuario")
    int reactivar(@Param("usuario") UUID usuario);
}
