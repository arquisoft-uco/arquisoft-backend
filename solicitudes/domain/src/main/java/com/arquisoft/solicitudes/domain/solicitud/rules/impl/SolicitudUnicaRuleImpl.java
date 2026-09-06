package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudDuplicadaException;
import com.arquisoft.solicitudes.domain.solicitud.model.DisponibilidadSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudUnicaRule;

public class SolicitudUnicaRuleImpl implements SolicitudUnicaRule {

    @Override
    public void validar(DisponibilidadSolicitud disponibilidad) {
        if (disponibilidad.yaExiste()) {
            throw new SolicitudDuplicadaException();
        }
    }
}
