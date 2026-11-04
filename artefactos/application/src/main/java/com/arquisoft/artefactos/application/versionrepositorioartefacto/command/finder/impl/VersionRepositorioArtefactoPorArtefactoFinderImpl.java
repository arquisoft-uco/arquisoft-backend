package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorArtefactoFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper.VersionRepositorioArtefactoMapper;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionRepositorioArtefactoPorArtefactoFinderImpl
        implements VersionRepositorioArtefactoPorArtefactoFinder {

    private final VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @Override
    public VersionRepositorioArtefactoDomain obtener(UUID artefacto) {
        return versionRepositorioArtefactoOutputPort.obtenerPorArtefacto(artefacto)
                .map(VersionRepositorioArtefactoMapper::toDomain)
                .orElse(VersionRepositorioArtefactoDomain.VACIO);
    }
}
