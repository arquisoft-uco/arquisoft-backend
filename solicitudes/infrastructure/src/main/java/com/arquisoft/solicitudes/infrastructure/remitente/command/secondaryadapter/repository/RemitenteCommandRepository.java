package com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.repository;

import com.arquisoft.solicitudes.infrastructure.remitente.command.secondaryadapter.entity.RemitenteJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RemitenteCommandRepository extends JpaRepository<RemitenteJpaEntity, UUID> {

    Optional<RemitenteJpaEntity> findByUsuarioId(UUID usuarioId);
}
