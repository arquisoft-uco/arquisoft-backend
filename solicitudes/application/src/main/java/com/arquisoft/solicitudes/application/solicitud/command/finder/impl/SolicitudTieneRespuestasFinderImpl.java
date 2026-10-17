package com.arquisoft.solicitudes.application.solicitud.command.finder.impl;

import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.application.solicitud.command.finder.SolicitudTieneRespuestasFinder;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SolicitudTieneRespuestasFinderImpl implements SolicitudTieneRespuestasFinder {

    private final RespuestaOutputPort respuestaOutputPort;

    @Override
    public Boolean obtener(UUID solicitud) {
        return respuestaOutputPort.existePorSolicitud(solicitud);
    }
}
