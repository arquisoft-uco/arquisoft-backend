package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadAsesorDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public interface EnviarSolicitudNovedadAsesorValidator {

    void validarExistenciaUsuarios(
            EnvioSolicitudNovedadAsesorDomain envio, UsuarioDomain remitente, UsuarioDomain destinatario);

    void validarAsignacionDestinatario(
            EnvioSolicitudNovedadAsesorDomain envio, boolean destinatarioAsignado);

    void validarUnicidad(DisponibilidadSolicitud disponibilidad);
}
