package com.arquisoft.artefactos.application.revisionasesor.command.finder.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionDeVersionExisteFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevisionDeVersionExisteFinderImpl implements RevisionDeVersionExisteFinder {

    private final RevisionAsesorOutputPort revisionAsesorOutputPort;

    @Override
    public Boolean obtener(UUID versionArtefacto) {
        return revisionAsesorOutputPort.existePorVersionArtefacto(versionArtefacto);
    }
}
