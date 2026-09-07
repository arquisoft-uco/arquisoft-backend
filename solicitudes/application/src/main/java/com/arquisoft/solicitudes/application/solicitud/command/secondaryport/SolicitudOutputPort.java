package com.arquisoft.solicitudes.application.solicitud.command.secondaryport;

import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.DatosSolicitudEntity;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.SolicitudEntity;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface SolicitudOutputPort {

    void registrar(SolicitudEntity solicitud);

    boolean existePorCombinacionUnica(
            UUID destinatario, UUID remitente, LocalDateTime fechaCreacion, String mensajeSolicitud);

    Optional<DatosSolicitudEntity> buscarDatos(UUID solicitudId);

    boolean tieneRespuestas(UUID solicitudId);

    void eliminar(UUID solicitudId);
}
