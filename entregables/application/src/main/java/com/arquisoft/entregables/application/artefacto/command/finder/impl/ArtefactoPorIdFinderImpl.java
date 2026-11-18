package com.arquisoft.entregables.application.artefacto.command.finder.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.mapper.ArtefactoMapper;
import com.arquisoft.entregables.domain.artefacto.ArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtefactoPorIdFinderImpl implements ArtefactoPorIdFinder {

    private final ArtefactoOutputPort artefactoOutputPort;

    @Override
    public ArtefactoDomain obtener(UUID artefacto) {
        return artefactoOutputPort.obtenerPorId(artefacto)
                .map(ArtefactoMapper::toDomain)
                .orElse(ArtefactoDomain.VACIO);
    }
}
