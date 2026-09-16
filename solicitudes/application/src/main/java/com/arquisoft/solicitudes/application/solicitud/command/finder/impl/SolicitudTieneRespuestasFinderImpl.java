package com.arquisoft.solicitudes.application.solicitud.command.finder.impl;

import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudTieneRespuestasFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SolicitudTieneRespuestasFinderImpl implements SolicitudTieneRespuestasFinder {

    private final SolicitudOutputPort solicitudOutputPort;

    @Override
    public Boolean obtener(UUID solicitud) {
        return solicitudOutputPort.tieneRespuestas(solicitud);
    }
}
