package com.arquisoft.artefactos.application.versionartefacto.query.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.finder.AsesorDeVersionArtefactoQueryFinder;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.VersionArtefactoAccesoQueryOutputPort;
import com.arquisoft.shared.util.UtilUUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorDeVersionArtefactoQueryFinderImpl implements AsesorDeVersionArtefactoQueryFinder {

    private final VersionArtefactoAccesoQueryOutputPort versionArtefactoAccesoQueryOutputPort;

    @Override
    public UUID obtener(UUID versionArtefacto) {
        return versionArtefactoAccesoQueryOutputPort.obtenerAsesorPorId(versionArtefacto)
                .orElse(UtilUUID.obtenerUUIDPorDefecto());
    }
}
