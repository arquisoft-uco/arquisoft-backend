package com.arquisoft.solicitudes.application.respuesta.command.validator;

import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import com.arquisoft.solicitudes.domain.usuario.UsuarioDomain;

import java.util.UUID;

public interface ResponderSolicitudNovedadCoordinadorValidator {

    void validar(UUID solicitud, ResumenSolicitud resumen,
                 UsuarioDomain remitente, UsuarioDomain coordinador,
                 UUID solicitante, boolean yaRespondida);
}
