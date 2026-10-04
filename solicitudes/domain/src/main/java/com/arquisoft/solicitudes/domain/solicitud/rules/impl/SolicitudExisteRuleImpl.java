package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEncontradaException;
import com.arquisoft.solicitudes.domain.solicitud.model.ExistenciaSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudExisteRule;

public class SolicitudExisteRuleImpl implements SolicitudExisteRule {

    @Override
    public void validar(ExistenciaSolicitud existencia) {
        if (!existencia.existe()) {
            throw new SolicitudNoEncontradaException(existencia.solicitud());
        }
    }
}
