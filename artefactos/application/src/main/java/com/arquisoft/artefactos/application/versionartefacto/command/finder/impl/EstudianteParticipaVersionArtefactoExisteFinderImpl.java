package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.finder.EstudianteParticipaVersionArtefactoExisteFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.artefactos.domain.versionartefacto.EliminacionVersionArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstudianteParticipaVersionArtefactoExisteFinderImpl
        implements EstudianteParticipaVersionArtefactoExisteFinder {

    private final VersionArtefactoOutputPort versionArtefactoOutputPort;

    @Override
    public Boolean obtener(EliminacionVersionArtefactoDomain eliminacion) {
        return versionArtefactoOutputPort.existeParticipacion(
                eliminacion.getVersionArtefacto(), eliminacion.getSolicitante());
    }
}
