package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.repository;

import com.arquisoft.notificaciones.application.notificacion.command.secondaryport.entity.NotificacionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificacionCommandRepository extends JpaRepository<NotificacionEntity, UUID> {

    boolean existsByIdEvento(String idEvento);
}
