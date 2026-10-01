package com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.repository;

import com.arquisoft.solicitudes.infrastructure.destinatario.command.secondaryadapter.entity.DestinatarioJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DestinatarioCommandRepository extends JpaRepository<DestinatarioJpaEntity, UUID> {

    Optional<DestinatarioJpaEntity> findByUsuarioId(UUID usuarioId);
}
