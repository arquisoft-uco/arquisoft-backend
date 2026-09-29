package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;

public interface EliminarRespuestaNovedadCoordinadorValidator {

    void validar(EliminacionRespuestaNovedadCoordinadorDomain entrada,
                 ResumenSolicitud resumenSolicitud, ResumenRespuesta resumenRespuesta);
}
