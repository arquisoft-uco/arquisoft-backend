package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase.CambiarNombreRepositorioVersionRepositorioArtefactoUseCase;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.CambioNombreRepositorioDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarNombreRepositorioVersionRepositorioArtefactoUseCaseImpl
        implements CambiarNombreRepositorioVersionRepositorioArtefactoUseCase {

    private final VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;

    @Override
    public void ejecutar(CambioNombreRepositorioDomain cambio) {
        versionRepositorioArtefactoOutputPort.actualizarNombreRepositorio(
                cambio.getRepositorio(), cambio.getNombreRepositorio());
    }
}
