package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoEsUltimaFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionArtefactoEsUltimaFinderImpl implements VersionArtefactoEsUltimaFinder {

    private final VersionArtefactoOutputPort versionArtefactoOutputPort;

    @Override
    public Boolean obtener(UUID versionArtefacto) {
        return versionArtefactoOutputPort.esUltimaDeSuArtefacto(versionArtefacto);
    }
}
