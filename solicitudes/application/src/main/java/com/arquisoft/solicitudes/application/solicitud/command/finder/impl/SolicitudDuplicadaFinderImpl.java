package com.arquisoft.solicitudes.application.solicitud.command.finder.impl;

import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudDuplicadaFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.domain.solicitud.model.ClaveSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SolicitudDuplicadaFinderImpl implements SolicitudDuplicadaFinder {

    private final SolicitudOutputPort solicitudOutputPort;

    @Override
    public Boolean obtener(ClaveSolicitud clave) {
        return solicitudOutputPort.existePorCombinacionUnica(
                clave.destinatario(), clave.remitente(), clave.fechaCreacion(), clave.mensajeSolicitud());
    }
}
