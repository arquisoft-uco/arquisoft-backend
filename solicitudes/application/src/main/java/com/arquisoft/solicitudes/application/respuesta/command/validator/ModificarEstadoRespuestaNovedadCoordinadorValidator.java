package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaNovedadCoordinadorDomain;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public interface ModificarEstadoRespuestaNovedadCoordinadorValidator {

    void validar(ModificacionEstadoRespuestaNovedadCoordinadorDomain entrada,
                 ResumenSolicitud resumenSolicitud, ResumenRespuesta resumenRespuesta,
                 UsuarioDomain remitente, UsuarioDomain coordinador);
}
