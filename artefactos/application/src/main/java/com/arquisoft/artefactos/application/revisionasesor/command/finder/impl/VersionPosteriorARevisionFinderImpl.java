package com.arquisoft.artefactos.application.revisionasesor.command.finder.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.finder.VersionPosteriorARevisionFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionPosteriorARevisionFinderImpl implements VersionPosteriorARevisionFinder {

    private final RevisionAsesorOutputPort revisionAsesorOutputPort;

    @Override
    public Boolean obtener(UUID revisionAsesor) {
        return revisionAsesorOutputPort.existeVersionPosterior(revisionAsesor);
    }
}
