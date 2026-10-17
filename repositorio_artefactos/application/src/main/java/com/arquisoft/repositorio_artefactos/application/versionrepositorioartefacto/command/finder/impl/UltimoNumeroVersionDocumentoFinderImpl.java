package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.UltimoNumeroVersionDocumentoFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UltimoNumeroVersionDocumentoFinderImpl implements UltimoNumeroVersionDocumentoFinder {

    private final VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @Override
    public Integer obtener(SubidaVersionRepositorioArtefactoDomain subida) {
        return versionRepositorioArtefactoOutputPort.obtenerUltimoNumeroVersion(
                subida.getRepositorio(), subida.getNombreDocumento());
    }
}
