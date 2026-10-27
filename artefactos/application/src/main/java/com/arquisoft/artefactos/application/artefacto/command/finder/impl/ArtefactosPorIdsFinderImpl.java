package com.arquisoft.artefactos.application.artefacto.command.finder.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactosPorIdsFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.mapper.ArtefactoMapper;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArtefactosPorIdsFinderImpl implements ArtefactosPorIdsFinder {

    private final ArtefactoOutputPort artefactoOutputPort;

    @Override
    public List<ArtefactoDomain> obtener(List<UUID> ids) {
        return artefactoOutputPort.obtenerPorIds(ids).stream()
                .map(ArtefactoMapper::toDomain)
                .toList();
    }
}
