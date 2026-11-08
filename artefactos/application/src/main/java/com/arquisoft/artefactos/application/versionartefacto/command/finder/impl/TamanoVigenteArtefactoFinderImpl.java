package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.finder.TamanoVigenteArtefactoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TamanoVigenteArtefactoFinderImpl implements TamanoVigenteArtefactoFinder {

    private final VersionArtefactoOutputPort versionArtefactoOutputPort;

    @Override
    public Long obtener(UUID artefacto) {
        return versionArtefactoOutputPort.tamanoVigenteDelArtefacto(artefacto, EstadoRevisionAsesor.CERRADA.getId());
    }
}
