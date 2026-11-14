package com.arquisoft.entregables.application.artefacto.command.finder.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ArtefactosVigentesProyectoFinder;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.mapper.ArtefactoMapper;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtefactosVigentesProyectoFinderImpl implements ArtefactosVigentesProyectoFinder {

    private final ArtefactoOutputPort artefactoOutputPort;

    @Override
    public List<ArtefactoDomain> obtener(UUID proyectoGrado) {
        return artefactoOutputPort.obtenerVigentesPorProyecto(proyectoGrado).stream()
                .map(ArtefactoMapper::toDomain)
                .toList();
    }
}
