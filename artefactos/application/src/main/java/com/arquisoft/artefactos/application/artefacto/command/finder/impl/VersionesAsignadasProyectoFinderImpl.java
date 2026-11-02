package com.arquisoft.artefactos.application.artefacto.command.finder.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.VersionesAsignadasProyectoFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper.VersionRepositorioArtefactoMapper;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class VersionesAsignadasProyectoFinderImpl implements VersionesAsignadasProyectoFinder {

    private final VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @Override
    public List<VersionRepositorioArtefactoDomain> obtener(UUID proyectoGrado) {
        return versionRepositorioArtefactoOutputPort.consultarVersionesAsignadas(proyectoGrado).stream()
                .map(VersionRepositorioArtefactoMapper::toDomain)
                .toList();
    }
}
