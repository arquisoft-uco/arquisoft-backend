package com.arquisoft.notificaciones.infrastructure.notificacion.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificacionRepository extends JpaRepository<NotificacionEntity, UUID> {

    boolean existsByIdEvento(String idEvento);
}
