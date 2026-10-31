package com.arquisoft.artefactos.application.versionartefacto.query.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.query.finder.EstudianteParticipaVersionArtefactoQueryFinder;
import com.arquisoft.artefactos.application.versionartefacto.query.finder.model.ParticipacionVersionArtefacto;
import com.arquisoft.artefactos.application.versionartefacto.query.secondaryport.VersionArtefactoAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstudianteParticipaVersionArtefactoQueryFinderImpl
        implements EstudianteParticipaVersionArtefactoQueryFinder {

    private final VersionArtefactoAccesoQueryOutputPort versionArtefactoAccesoQueryOutputPort;

    @Override
    public Boolean obtener(ParticipacionVersionArtefacto entrada) {
        return versionArtefactoAccesoQueryOutputPort.existeParticipacion(
                entrada.versionArtefacto(), entrada.estudiante());
    }
}
