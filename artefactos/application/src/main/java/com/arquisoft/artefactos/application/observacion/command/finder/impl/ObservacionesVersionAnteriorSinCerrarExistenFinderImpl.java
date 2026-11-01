package com.arquisoft.artefactos.application.observacion.command.finder.impl;

import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionesVersionAnteriorSinCerrarExistenFinder;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.domain.observacion.model.EstadoObservacion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionesVersionAnteriorSinCerrarExistenFinderImpl
        implements ObservacionesVersionAnteriorSinCerrarExistenFinder {

    private final ObservacionOutputPort observacionOutputPort;

    @Override
    public Boolean obtener(UUID revisionAsesor) {
        return observacionOutputPort.existenNoCerradasDeVersionAnterior(
                revisionAsesor, EstadoObservacion.CERRADA.getId());
    }
}
