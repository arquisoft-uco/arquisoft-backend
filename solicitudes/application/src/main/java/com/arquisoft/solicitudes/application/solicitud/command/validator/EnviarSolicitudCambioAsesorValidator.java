package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudCambioAsesorDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public interface EnviarSolicitudCambioAsesorValidator {

    void validarExistenciaUsuarios(
            EnvioSolicitudCambioAsesorDomain envio, UsuarioDomain remitente, UsuarioDomain destinatario);

    void validarAsignacionDestinatario(
            EnvioSolicitudCambioAsesorDomain envio, boolean destinatarioAsignado);

    void validarUnicidad(DisponibilidadSolicitud disponibilidad);
}
