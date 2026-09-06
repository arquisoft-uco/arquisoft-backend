package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.domain.solicitud.EnvioSolicitudNovedadAsesorDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;

public interface EnviarSolicitudNovedadAsesorValidator {

    void validarExistenciaUsuarios(
            EnvioSolicitudNovedadAsesorDomain envio, boolean remitenteExiste, boolean destinatarioExiste);

    void validarAsignacionDestinatario(
            EnvioSolicitudNovedadAsesorDomain envio, boolean destinatarioAsignado);

    void validarUnicidad(DisponibilidadSolicitud disponibilidad);
}
