package com.arquisoft.solicitudes.application.solicitud.command.finder.impl;

import com.arquisoft.solicitudes.application.solicitud.command.finder.DatosSolicitudFinder;
import com.arquisoft.solicitudes.application.solicitud.command.secondaryport.SolicitudOutputPort;
import com.arquisoft.solicitudes.domain.solicitud.model.ResumenSolicitud;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DatosSolicitudFinderImpl implements DatosSolicitudFinder {

    private final SolicitudOutputPort solicitudOutputPort;

    @Override
    public Optional<ResumenSolicitud> obtener(UUID solicitud) {
        return solicitudOutputPort.buscarDatos(solicitud)
                .map(datos -> new ResumenSolicitud(
                        solicitud, datos.remitenteUsuario(), datos.tipoSolicitud()));
    }
}
