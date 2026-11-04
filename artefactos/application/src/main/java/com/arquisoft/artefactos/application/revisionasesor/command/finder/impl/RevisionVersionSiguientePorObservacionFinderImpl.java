package com.arquisoft.artefactos.application.revisionasesor.command.finder.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionVersionSiguientePorObservacionFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.mapper.RevisionAsesorMapper;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevisionVersionSiguientePorObservacionFinderImpl implements RevisionVersionSiguientePorObservacionFinder {

    private final RevisionAsesorOutputPort revisionAsesorOutputPort;

    @Override
    public RevisionAsesorDomain obtener(UUID observacion) {
        return revisionAsesorOutputPort.obtenerBloqueadaDeVersionSiguientePorObservacion(observacion)
                .map(RevisionAsesorMapper::toDomain)
                .orElse(RevisionAsesorDomain.VACIO);
    }
}
