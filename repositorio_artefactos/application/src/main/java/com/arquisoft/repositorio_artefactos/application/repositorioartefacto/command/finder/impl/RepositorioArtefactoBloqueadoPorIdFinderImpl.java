package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.RepositorioArtefactoBloqueadoPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.mapper.RepositorioArtefactoMapper;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RepositorioArtefactoBloqueadoPorIdFinderImpl implements RepositorioArtefactoBloqueadoPorIdFinder {

    private final RepositorioArtefactoOutputPort repositorioArtefactoOutputPort;

    @Override
    public RepositorioArtefactoDomain obtener(UUID id) {
        return repositorioArtefactoOutputPort.obtenerBloqueadoPorId(id)
                .map(RepositorioArtefactoMapper::toDomain)
                .orElse(RepositorioArtefactoDomain.VACIO);
    }
}
