package com.arquisoft.artefactos.application.versionartefacto.query.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.finder.ArchivoVersionArtefactoQueryFinder;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.VersionArtefactoAccesoQueryOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.mapper.ArchivoVersionArtefactoMapper;
import com.arquisoft.artefactos.domain.versionartefacto.ArchivoVersionArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArchivoVersionArtefactoQueryFinderImpl implements ArchivoVersionArtefactoQueryFinder {

    private final VersionArtefactoAccesoQueryOutputPort versionArtefactoAccesoQueryOutputPort;

    @Override
    public ArchivoVersionArtefactoDomain obtener(UUID versionArtefacto) {
        return versionArtefactoAccesoQueryOutputPort.obtenerArchivoPorId(versionArtefacto)
                .map(ArchivoVersionArtefactoMapper::toDomain)
                .orElse(ArchivoVersionArtefactoDomain.VACIO);
    }
}
