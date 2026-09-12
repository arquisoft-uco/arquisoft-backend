package com.arquisoft.solicitudes.domain.respuesta.rules.impl;

import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEnRevisionException;
import com.arquisoft.solicitudes.domain.respuesta.model.EstadoRespuestaActual;
import com.arquisoft.solicitudes.domain.respuesta.rules.RespuestaEnRevisionRule;

public class RespuestaEnRevisionRuleImpl implements RespuestaEnRevisionRule {

    @Override
    public void validar(EstadoRespuestaActual actual) {
        if (!EstadoRespuesta.EN_REVISION.getId().equals(actual.estado())) {
            throw new RespuestaNoEnRevisionException(actual.solicitud());
        }
    }
}
