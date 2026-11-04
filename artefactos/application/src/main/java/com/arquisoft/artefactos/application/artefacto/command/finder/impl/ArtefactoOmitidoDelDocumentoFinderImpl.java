package com.arquisoft.artefactos.application.artefacto.command.finder.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoOmitidoDelDocumentoFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.mapper.ArtefactoMapper;
import com.arquisoft.artefactos.domain.artefacto.ArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ArtefactoOmitidoDelDocumentoFinderImpl implements ArtefactoOmitidoDelDocumentoFinder {

    private final ArtefactoOutputPort artefactoOutputPort;

    @Override
    public ArtefactoDomain obtener(ArtefactoDomain artefacto) {
        return artefactoOutputPort.obtenerOmitidoPorProyectoYDocumento(
                        artefacto.getProyectoGrado(), artefacto.getVersionRepositorioArtefacto())
                .map(ArtefactoMapper::toDomain)
                .orElse(ArtefactoDomain.VACIO);
    }
}
