package com.arquisoft.artefactos.application.observacion.command.finder.impl;

import com.arquisoft.artefactos.application.observacion.command.finder.DescripcionObservacionEnRevisionExisteFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.model.DescripcionEnRevision;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DescripcionObservacionEnRevisionExisteFinderImpl implements DescripcionObservacionEnRevisionExisteFinder {

    private final ObservacionOutputPort observacionOutputPort;

    @Override
    public Boolean obtener(DescripcionEnRevision descripcionEnRevision) {
        return observacionOutputPort.existePorRevisionAsesorYDescripcion(
                descripcionEnRevision.revisionAsesor(), descripcionEnRevision.descripcion());
    }
}
