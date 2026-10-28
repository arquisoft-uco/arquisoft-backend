package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.finder.AnalisisCargaFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AnalisisArchivoOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.mapper.AnalisisArchivoMapper;
import com.arquisoft.artefactos.domain.versionartefacto.AnalisisArchivoDomain;
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
