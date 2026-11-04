package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.ArchivoVersionPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper.VersionRepositorioArtefactoMapper;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.ArchivoVersionDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArchivoVersionPorIdFinderImpl implements ArchivoVersionPorIdFinder {

    private final VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @Override
    public ArchivoVersionDomain obtener(UUID id) {
        return versionRepositorioArtefactoOutputPort.obtenerPorId(id)
                .map(VersionRepositorioArtefactoMapper::toArchivoDomain)
                .orElse(ArchivoVersionDomain.VACIO);
    }
}
