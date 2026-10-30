package com.arquisoft.artefactos.application.observacion.command.finder.impl;

import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionDescripcionExisteFinder;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ObservacionDescripcionExisteFinderImpl implements ObservacionDescripcionExisteFinder {

    private final ObservacionOutputPort observacionOutputPort;

    @Override
    public Boolean obtener(ObservacionDomain observacion) {
        return observacionOutputPort.existePorRevisionAsesorYDescripcion(
                observacion.getRevisionAsesor(), observacion.getDescripcion());
    }
}
