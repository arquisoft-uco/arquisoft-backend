package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudCambioAsesorDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;

public interface EnviarSolicitudCambioAsesorValidator {

    void validarExistenciaUsuarios(
            EnvioSolicitudCambioAsesorDomain envio, boolean remitenteExiste, boolean destinatarioExiste);

    void validarAsignacionDestinatario(
            EnvioSolicitudCambioAsesorDomain envio, boolean destinatarioAsignado);

    void validarUnicidad(DisponibilidadSolicitud disponibilidad);
}
