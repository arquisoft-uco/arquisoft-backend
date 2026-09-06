package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;

public interface EnviarSolicitudNovedadCoordinadorValidator {

    void validarExistenciaUsuarios(
            EnvioSolicitudNovedadCoordinadorDomain envio, boolean remitenteExiste, boolean destinatarioExiste);

    void validarAsignacionDestinatario(
            EnvioSolicitudNovedadCoordinadorDomain envio, boolean destinatarioAsignado);

    void validarUnicidad(DisponibilidadSolicitud disponibilidad);
}
