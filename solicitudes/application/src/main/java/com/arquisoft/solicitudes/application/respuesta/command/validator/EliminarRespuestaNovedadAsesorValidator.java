package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.domain.respuesta.EliminacionRespuestaNovedadAsesorDomain;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;

public interface EliminarRespuestaNovedadAsesorValidator {

    void validar(EliminacionRespuestaNovedadAsesorDomain entrada,
                 ResumenSolicitud resumenSolicitud, ResumenRespuesta resumenRespuesta);
}
