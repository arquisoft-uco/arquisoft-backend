package com.arquisoft.solicitudes.application.respuesta.command.finder.impl;

import com.arquisoft.solicitudes.application.respuesta.command.finder.DatosRespuestaFinder;
import com.arquisoft.solicitudes.application.respuesta.command.secondaryport.RespuestaOutputPort;
import com.arquisoft.solicitudes.domain.respuesta.model.ResumenRespuesta;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DatosRespuestaFinderImpl implements DatosRespuestaFinder {

    private final RespuestaOutputPort respuestaOutputPort;

    @Override
    public ResumenRespuesta obtener(UUID solicitud) {
        return respuestaOutputPort.buscarEstadoPorSolicitud(solicitud)
                .map(estado -> new ResumenRespuesta(solicitud, estado))
                .orElse(ResumenRespuesta.VACIO);
    }
}
