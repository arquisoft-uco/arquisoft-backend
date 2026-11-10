package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.finder.UrisArchivoVersionesProyectoGradoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UrisArchivoVersionesProyectoGradoFinderImpl implements UrisArchivoVersionesProyectoGradoFinder {

    private final VersionArtefactoOutputPort versionArtefactoOutputPort;

    @Override
    public List<String> obtener(UUID proyectoGrado) {
        return versionArtefactoOutputPort.obtenerUrisArchivoPorProyectoGrado(proyectoGrado);
    }
}
