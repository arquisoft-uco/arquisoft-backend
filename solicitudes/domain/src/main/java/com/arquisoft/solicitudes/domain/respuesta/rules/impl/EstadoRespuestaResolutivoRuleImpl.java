package com.arquisoft.solicitudes.domain.respuesta.rules.impl;

import com.arquisoft.solicitudes.domain.estadorespuesta.EstadoRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.exception.EstadoRespuestaNoResolutivoException;
import com.arquisoft.solicitudes.domain.respuesta.model.NuevoEstadoRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.rules.EstadoRespuestaResolutivoRule;

public class EstadoRespuestaResolutivoRuleImpl implements EstadoRespuestaResolutivoRule {

    @Override
    public void validar(NuevoEstadoRespuesta entrada) {
        if (!EstadoRespuesta.esValido(entrada.nuevoEstado())
                || EstadoRespuesta.EN_REVISION.getId().equals(entrada.nuevoEstado())) {
            throw new EstadoRespuestaNoResolutivoException(entrada.solicitud(), entrada.nuevoEstado());
        }
    }
}
