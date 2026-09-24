package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudConRespuestasException;
import com.arquisoft.solicitudes.domain.solicitud.model.RespuestasSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudSinRespuestasRule;

public class SolicitudSinRespuestasRuleImpl implements SolicitudSinRespuestasRule {

    @Override
    public void validar(RespuestasSolicitud respuestas) {
        if (respuestas.tieneRespuestas()) {
            throw new SolicitudConRespuestasException(respuestas.solicitud());
        }
    }
}
