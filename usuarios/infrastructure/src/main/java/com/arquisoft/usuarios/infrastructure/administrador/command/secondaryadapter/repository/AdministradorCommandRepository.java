package com.arquisoft.usuarios.infrastructure.administrador.command.secondaryadapter.repository;

import com.arquisoft.usuarios.infrastructure.administrador.command.secondaryadapter.entity.AdministradorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AdministradorCommandRepository extends JpaRepository<AdministradorJpaEntity, UUID> {

    @Modifying(clearAutomatically = true)
    @Query("UPDATE AdministradorJpaEntity a SET a.eliminadoEn = NULL WHERE a.usuarioId = :usuario")
    int reactivar(@Param("usuario") UUID usuario);
}
