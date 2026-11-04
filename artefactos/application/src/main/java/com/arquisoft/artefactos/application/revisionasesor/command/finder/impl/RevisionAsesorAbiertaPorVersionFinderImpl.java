package com.arquisoft.artefactos.application.revisionasesor.command.finder.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorAbiertaPorVersionFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevisionAsesorAbiertaPorVersionFinderImpl implements RevisionAsesorAbiertaPorVersionFinder {

    private final RevisionAsesorOutputPort revisionAsesorOutputPort;

    @Override
    public Boolean obtener(UUID versionArtefacto) {
        return revisionAsesorOutputPort.existeAbiertaPorVersionArtefacto(versionArtefacto);
    }
}
