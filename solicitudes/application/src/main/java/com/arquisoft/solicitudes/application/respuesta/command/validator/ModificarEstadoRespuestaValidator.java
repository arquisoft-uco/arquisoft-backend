package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.domain.respuesta.ModificacionEstadoRespuestaDomain;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public interface ModificarEstadoRespuestaValidator {

    void validar(ModificacionEstadoRespuestaDomain entrada,
                 ResumenSolicitud resumenSolicitud, ResumenRespuesta resumenRespuesta,
                 UsuarioDomain remitente, UsuarioDomain responsable);
}
