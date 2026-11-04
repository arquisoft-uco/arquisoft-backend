package com.arquisoft.artefactos.application.artefacto.command.finder.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorVersionRepositorioExisteFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtefactoPorVersionRepositorioExisteFinderImpl implements ArtefactoPorVersionRepositorioExisteFinder {

    private final ArtefactoOutputPort artefactoOutputPort;

    @Override
    public Boolean obtener(UUID versionRepositorioArtefacto) {
        return artefactoOutputPort.existePorVersionRepositorioArtefacto(versionRepositorioArtefacto);
    }
}
