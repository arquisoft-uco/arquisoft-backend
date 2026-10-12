package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.domain.respuesta.RespuestaSolicitudDomain;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

public interface ResponderSolicitudValidator {

    void validar(RespuestaSolicitudDomain entrada, ResumenSolicitud resumen,
                 UsuarioDomain remitente, UsuarioDomain responsable, boolean yaRespondida);
}
