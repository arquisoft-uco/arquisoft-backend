package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoBloqueadaPorIdFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.mapper.VersionArtefactoMapper;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionArtefactoBloqueadaPorIdFinderImpl implements VersionArtefactoBloqueadaPorIdFinder {

    private final VersionArtefactoOutputPort versionArtefactoOutputPort;

    @Override
    public VersionArtefactoDomain obtener(UUID id) {
        return versionArtefactoOutputPort.obtenerBloqueadaPorId(id)
                .map(VersionArtefactoMapper::toDomain)
                .orElse(VersionArtefactoDomain.VACIO);
    }
}
