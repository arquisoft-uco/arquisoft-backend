package com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.repository;

import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.RespuestaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RespuestaCommandRepository extends JpaRepository<RespuestaJpaEntity, UUID> {

    boolean existsBySolicitudId(UUID solicitudId);
}
