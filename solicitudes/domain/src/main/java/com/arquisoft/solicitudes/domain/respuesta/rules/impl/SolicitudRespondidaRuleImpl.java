package com.arquisoft.solicitudes.domain.respuesta.rules.impl;

import com.arquisoft.solicitudes.domain.respuesta.exception.SolicitudYaRespondidaException;
import com.arquisoft.solicitudes.domain.respuesta.model.RespuestaSolicitud;
import com.arquisoft.solicitudes.domain.respuesta.rules.SolicitudRespondidaRule;

public class SolicitudRespondidaRuleImpl implements SolicitudRespondidaRule {

    @Override
    public void validar(RespuestaSolicitud respuesta) {
        if (respuesta.yaRespondida()) {
            throw new SolicitudYaRespondidaException(respuesta.solicitud());
        }
    }
}
