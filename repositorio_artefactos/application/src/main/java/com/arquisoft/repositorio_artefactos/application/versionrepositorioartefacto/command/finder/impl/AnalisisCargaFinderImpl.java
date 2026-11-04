package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.AnalisisCargaFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.AnalisisArchivoOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper.AnalisisArchivoMapper;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.AnalisisArchivoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AnalisisCargaFinderImpl implements AnalisisCargaFinder {

    private final AnalisisArchivoOutputPort analisisArchivoOutputPort;

    @Override
    public AnalisisArchivoDomain obtener(UUID carga) {
        return analisisArchivoOutputPort.analizarCarga(carga)
                .map(AnalisisArchivoMapper::toDomain)
                .orElse(AnalisisArchivoDomain.VACIO);
    }
}
