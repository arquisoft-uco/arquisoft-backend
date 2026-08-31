package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudCambioAsesorDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;

public interface EnviarSolicitudCambioAsesorValidator {

    void validarExistenciaUsuarios(
            EnvioSolicitudCambioAsesorDomain envio, boolean remitenteExiste, boolean destinatarioExiste);

    void validarUnicidad(DisponibilidadSolicitud disponibilidad);
}
