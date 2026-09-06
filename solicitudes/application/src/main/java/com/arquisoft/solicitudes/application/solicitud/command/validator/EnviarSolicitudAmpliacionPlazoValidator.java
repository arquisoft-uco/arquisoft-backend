package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudAmpliacionPlazoDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;

public interface EnviarSolicitudAmpliacionPlazoValidator {

    void validarExistenciaUsuarios(
            EnvioSolicitudAmpliacionPlazoDomain envio, boolean remitenteExiste, boolean destinatarioExiste);

    void validarAsignacionDestinatario(
            EnvioSolicitudAmpliacionPlazoDomain envio, boolean destinatarioAsignado);

    void validarUnicidad(DisponibilidadSolicitud disponibilidad);
}
