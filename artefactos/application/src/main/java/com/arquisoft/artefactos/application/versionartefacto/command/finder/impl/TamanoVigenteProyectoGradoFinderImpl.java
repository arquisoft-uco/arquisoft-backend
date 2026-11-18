package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.finder.TamanoVigenteProyectoGradoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TamanoVigenteProyectoGradoFinderImpl implements TamanoVigenteProyectoGradoFinder {

    private final VersionArtefactoOutputPort versionArtefactoOutputPort;

    @Override
    public Long obtener(UUID artefacto) {
        return versionArtefactoOutputPort.tamanoVigenteDelProyectoDeArtefacto(artefacto, EstadoRevisionAsesor.CERRADA.getId());
    }
}
