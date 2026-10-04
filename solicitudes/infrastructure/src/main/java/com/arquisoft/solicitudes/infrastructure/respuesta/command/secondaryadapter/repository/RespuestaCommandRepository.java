package com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.repository;

import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.EstadoRespuestaJpaEntity;
import com.arquisoft.solicitudes.infrastructure.respuesta.command.secondaryadapter.entity.RespuestaJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RespuestaCommandRepository extends JpaRepository<RespuestaJpaEntity, UUID> {

    boolean existsBySolicitudId(UUID solicitudId);

    @Query("SELECT r.estadoRespuesta.id FROM RespuestaJpaEntity r WHERE r.solicitudId = :solicitudId")
    Optional<String> buscarEstadoPorSolicitud(@Param("solicitudId") UUID solicitudId);

    void deleteBySolicitudId(UUID solicitudId);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE RespuestaJpaEntity r SET r.estadoRespuesta = :estadoRespuesta WHERE r.solicitudId = :solicitudId")
    int actualizarEstadoPorSolicitud(@Param("solicitudId") UUID solicitudId,
                                     @Param("estadoRespuesta") EstadoRespuestaJpaEntity estadoRespuesta);
}
