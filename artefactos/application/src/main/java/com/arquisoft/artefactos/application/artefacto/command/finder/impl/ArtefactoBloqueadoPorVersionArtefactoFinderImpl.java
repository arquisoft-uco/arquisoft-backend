package com.arquisoft.artefactos.application.artefacto.command.finder.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoBloqueadoPorVersionArtefactoFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.mapper.ArtefactoMapper;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtefactoBloqueadoPorVersionArtefactoFinderImpl implements ArtefactoBloqueadoPorVersionArtefactoFinder {

    private final ArtefactoOutputPort artefactoOutputPort;

    @Override
    public ArtefactoDomain obtener(UUID versionArtefacto) {
        return artefactoOutputPort.obtenerBloqueadoPorVersionArtefacto(versionArtefacto)
                .map(ArtefactoMapper::toDomain)
                .orElse(ArtefactoDomain.VACIO);
    }
}
