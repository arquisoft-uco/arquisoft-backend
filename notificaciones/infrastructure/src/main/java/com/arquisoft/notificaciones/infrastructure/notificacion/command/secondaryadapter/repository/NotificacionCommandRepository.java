package com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.repository;

import com.arquisoft.notificaciones.infrastructure.notificacion.command.secondaryadapter.entity.NotificacionJpaEntity;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificacionCommandRepository extends JpaRepository<NotificacionJpaEntity, UUID> {

    boolean existsByIdEvento(String idEvento);

    List<NotificacionJpaEntity> findByEstadoAndIntentosLessThanOrderByFechaCreacionAsc(
            String estado, int maxIntentos, Limit limite);
}
