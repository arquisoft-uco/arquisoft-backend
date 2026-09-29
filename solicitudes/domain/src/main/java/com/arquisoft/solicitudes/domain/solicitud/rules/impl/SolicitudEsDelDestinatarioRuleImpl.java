package com.arquisoft.solicitudes.domain.solicitud.rules.impl;

import com.arquisoft.solicitudes.domain.solicitud.exception.SolicitudNoEsDestinatarioException;
import com.arquisoft.solicitudes.domain.solicitud.model.PropiedadDestinatarioSolicitud;
import com.arquisoft.solicitudes.domain.solicitud.rules.SolicitudEsDelDestinatarioRule;

public class SolicitudEsDelDestinatarioRuleImpl implements SolicitudEsDelDestinatarioRule {

    @Override
    public void validar(PropiedadDestinatarioSolicitud propiedad) {
        if (!propiedad.destinatarioUsuario().equals(propiedad.solicitante())) {
            throw new SolicitudNoEsDestinatarioException(propiedad.solicitante());
        }
    }
}
