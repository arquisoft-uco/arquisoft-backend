package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoBloqueadaPorIdFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper.VersionRepositorioArtefactoMapper;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionRepositorioArtefactoBloqueadaPorIdFinderImpl
        implements VersionRepositorioArtefactoBloqueadaPorIdFinder {

    private final VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @Override
    public VersionRepositorioArtefactoDomain obtener(UUID id) {
        return versionRepositorioArtefactoOutputPort.obtenerBloqueadaPorId(id)
                .map(VersionRepositorioArtefactoMapper::toDomain)
                .orElse(VersionRepositorioArtefactoDomain.VACIO);
    }
}
