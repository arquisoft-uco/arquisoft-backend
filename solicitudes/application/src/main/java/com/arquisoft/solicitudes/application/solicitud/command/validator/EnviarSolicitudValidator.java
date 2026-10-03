package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;

public interface EnviarSolicitudValidator {

    void validar(EnvioSolicitudDomain envio, boolean destinatarioAsignado,
                 DisponibilidadSolicitud disponibilidad);
}
