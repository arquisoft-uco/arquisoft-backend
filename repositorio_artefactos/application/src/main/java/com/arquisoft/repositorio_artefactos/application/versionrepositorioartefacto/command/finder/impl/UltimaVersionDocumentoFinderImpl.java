package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.UltimaVersionDocumentoFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper.VersionRepositorioArtefactoMapper;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.model.UltimaVersionDocumento;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UltimaVersionDocumentoFinderImpl implements UltimaVersionDocumentoFinder {

    private final VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @Override
    public UltimaVersionDocumento obtener(SubidaVersionRepositorioArtefactoDomain subida) {
        return versionRepositorioArtefactoOutputPort.obtenerUltimaVersionDocumento(
                        subida.getRepositorio(), subida.getNombreDocumento())
                .map(VersionRepositorioArtefactoMapper::toUltimaVersion)
                .orElse(UltimaVersionDocumento.VACIO);
    }
}
