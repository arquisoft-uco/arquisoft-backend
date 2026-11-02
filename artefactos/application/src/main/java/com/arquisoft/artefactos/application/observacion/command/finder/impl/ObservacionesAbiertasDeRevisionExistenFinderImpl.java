package com.arquisoft.artefactos.application.observacion.command.finder.impl;

import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionesAbiertasDeRevisionExistenFinder;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionesAbiertasDeRevisionExistenFinderImpl implements ObservacionesAbiertasDeRevisionExistenFinder {

    private final ObservacionOutputPort observacionOutputPort;

    @Override
    public Boolean obtener(UUID revisionAsesor) {
        var estadosAbiertos = List.of(
                EstadoObservacion.NUEVA.getId(),
                EstadoObservacion.EN_PROGRESO.getId(),
                EstadoObservacion.REABIERTA.getId());
        return observacionOutputPort.existenPorRevisionAsesorYEstados(revisionAsesor, estadosAbiertos);
    }
}
