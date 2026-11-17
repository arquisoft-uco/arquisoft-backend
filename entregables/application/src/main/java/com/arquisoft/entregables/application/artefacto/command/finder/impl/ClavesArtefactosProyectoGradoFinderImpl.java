package com.arquisoft.entregables.application.artefacto.command.finder.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ClavesArtefactosProyectoGradoFinder;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ClavesArtefactosProyectoGradoFinderImpl implements ClavesArtefactosProyectoGradoFinder {

    private final ArtefactoOutputPort artefactoOutputPort;

    @Override
    public List<String> obtener(UUID proyectoGrado) {
        return artefactoOutputPort.obtenerClavesPorProyecto(proyectoGrado);
    }
}
