package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudAmpliacionPlazoDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public interface EnviarSolicitudAmpliacionPlazoValidator {

    void validarExistenciaUsuarios(
            EnvioSolicitudAmpliacionPlazoDomain envio, UsuarioDomain remitente, UsuarioDomain destinatario);

    void validarAsignacionDestinatario(
            EnvioSolicitudAmpliacionPlazoDomain envio, boolean destinatarioAsignado);

    void validarUnicidad(DisponibilidadSolicitud disponibilidad);
}
