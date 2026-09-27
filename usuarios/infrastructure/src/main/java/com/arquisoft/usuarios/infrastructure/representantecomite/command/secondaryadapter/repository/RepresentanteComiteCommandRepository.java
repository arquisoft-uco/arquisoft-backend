package com.arquisoft.usuarios.infrastructure.representantecomite.command.secondaryadapter.repository;

import com.arquisoft.usuarios.infrastructure.representantecomite.command.secondaryadapter.entity.RepresentanteComiteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface RepresentanteComiteCommandRepository extends JpaRepository<RepresentanteComiteJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE RepresentanteComiteJpaEntity r SET r.eliminadoEn = NULL WHERE r.usuarioId = :usuario")
    int reactivar(@Param("usuario") UUID usuario);
}
