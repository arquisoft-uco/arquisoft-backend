package com.arquisoft.solicitudes.application.solicitud.command.validator;

import com.arquisoft.solicitudes.domain.solicitud.EliminacionSolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;

public interface EliminarSolicitudValidator {

    void validar(EliminacionSolicitudDomain entrada, ResumenSolicitud resumen, boolean tieneRespuestas);
}
