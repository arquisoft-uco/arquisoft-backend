package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.finder.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.finder.ArchivoVersionQueryFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.VersionRepositorioArtefactoAccesoQueryOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.query.secondaryport.mapper.ArchivoVersionMapper;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.ArchivoVersionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArchivoVersionQueryFinderImpl implements ArchivoVersionQueryFinder {

    private final VersionRepositorioArtefactoAccesoQueryOutputPort versionRepositorioArtefactoAccesoQueryOutputPort;

    @Override
    public ArchivoVersionDomain obtener(UUID version) {
        return versionRepositorioArtefactoAccesoQueryOutputPort.obtenerArchivoPorId(version)
                .map(ArchivoVersionMapper::toDomain)
                .orElse(ArchivoVersionDomain.VACIO);
    }
}
