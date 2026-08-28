package com.arquisoft.solicitudes.application.solicitud.command.secondaryport;

import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.entity.SolicitudEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SolicitudOutputPort {

    void registrar(SolicitudEntity solicitud);

    boolean existePorCombinacionUnica(
            UUID destinatario, UUID remitente, LocalDateTime fechaCreacion, String mensajeSolicitud);
}
