package com.arquisoft.solicitudes.infrastructure.solicitud.command.secondaryadapter.repository;

import com.arquisoft.solicitudes.infrastructure.solicitud.command.secondaryadapter.entity.SolicitudJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SolicitudCommandRepository extends JpaRepository<SolicitudJpaEntity, UUID> {

    @Query("SELECT COUNT(s) > 0 FROM SolicitudJpaEntity s "
            + "WHERE s.destinatario.id = :destinatario AND s.remitente.id = :remitente "
            + "AND s.fechaCreacion = :fechaCreacion AND s.mensajeSolicitud = :mensajeSolicitud")
    boolean existePorCombinacionUnica(
            @Param("destinatario") UUID destinatario,
            @Param("remitente") UUID remitente,
            @Param("fechaCreacion") LocalDateTime fechaCreacion,
            @Param("mensajeSolicitud") String mensajeSolicitud);
}
