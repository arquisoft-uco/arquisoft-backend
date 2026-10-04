package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;

public interface EliminarRespuestaValidator {

    void validar(EliminacionRespuestaDomain entrada,
                 ResumenSolicitud resumenSolicitud, ResumenRespuesta resumenRespuesta);
}
