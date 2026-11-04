package com.arquisoft.artefactos.application.artefacto.command.finder.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.mapper.ArtefactoMapper;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtefactoPorIdFinderImpl implements ArtefactoPorIdFinder {

    private final ArtefactoOutputPort artefactoOutputPort;

    @Override
    public ArtefactoDomain obtener(UUID id) {
        return artefactoOutputPort.obtenerPorId(id)
                .map(ArtefactoMapper::toDomain)
                .orElse(ArtefactoDomain.VACIO);
    }
}
