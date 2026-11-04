package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.mapper.VersionArtefactoMapper;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionArtefactoPorIdFinderImpl implements VersionArtefactoPorIdFinder {

    private final VersionArtefactoOutputPort versionArtefactoOutputPort;

    @Override
    public VersionArtefactoDomain obtener(UUID id) {
        return versionArtefactoOutputPort.obtenerPorId(id)
                .map(VersionArtefactoMapper::toDomain)
                .orElse(VersionArtefactoDomain.VACIO);
    }
}
