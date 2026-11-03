package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.UltimaVersionDocumentoDeVersionRepositorioFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UltimaVersionDocumentoDeVersionRepositorioFinderImpl
        implements UltimaVersionDocumentoDeVersionRepositorioFinder {

    private final VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @Override
    public Integer obtener(UUID version) {
        return versionRepositorioArtefactoOutputPort.obtenerUltimaVersionDocumentoDe(version);
    }
}
