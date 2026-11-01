package com.arquisoft.artefactos.application.observacion.command.finder.impl;

import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionesDeRevisionExistenFinder;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionesDeRevisionExistenFinderImpl implements ObservacionesDeRevisionExistenFinder {

    private final ObservacionOutputPort observacionOutputPort;

    @Override
    public Boolean obtener(UUID revisionAsesor) {
        return observacionOutputPort.existenPorRevisionAsesor(revisionAsesor);
    }
}
