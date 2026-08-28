package com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.repository;

import com.arquisoft.solicitudes.infrastructure.usuario.command.secondaryadapter.entity.UsuarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UsuarioCommandRepository extends JpaRepository<UsuarioJpaEntity, UUID> {
}
