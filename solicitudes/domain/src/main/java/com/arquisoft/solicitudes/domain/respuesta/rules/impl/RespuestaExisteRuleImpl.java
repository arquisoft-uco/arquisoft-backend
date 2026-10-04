package com.arquisoft.solicitudes.domain.respuesta.rules.impl;

import com.arquisoft.solicitudes.domain.respuesta.exception.RespuestaNoEncontradaException;
import com.arquisoft.solicitudes.domain.respuesta.model.ExistenciaRespuesta;
import com.arquisoft.solicitudes.domain.respuesta.rules.RespuestaExisteRule;

public class RespuestaExisteRuleImpl implements RespuestaExisteRule {

    @Override
    public void validar(ExistenciaRespuesta existencia) {
        if (!existencia.existe()) {
            throw new RespuestaNoEncontradaException(existencia.solicitud());
        }
    }
}
