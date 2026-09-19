package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public interface EnviarSolicitudNovedadCoordinadorValidator {

    void validarExistenciaUsuarios(
            EnvioSolicitudNovedadCoordinadorDomain envio, UsuarioDomain remitente, UsuarioDomain destinatario);

    void validarAsignacionDestinatario(
            EnvioSolicitudNovedadCoordinadorDomain envio, boolean destinatarioAsignado);

    void validarUnicidad(DisponibilidadSolicitud disponibilidad);
}
