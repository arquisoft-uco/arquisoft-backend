package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoPropiaException;
import com.arquisoft.solicitudes.domain.solicitud.model.PropiedadSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsDelRemitenteRule;

public class SolicitudEsDelRemitenteRuleImpl implements SolicitudEsDelRemitenteRule {

    @Override
    public void validar(PropiedadSolicitud propiedad) {
        if (!propiedad.remitenteUsuario().equals(propiedad.solicitante())) {
            throw new SolicitudNoPropiaException(propiedad.solicitud());
        }
    }
}
