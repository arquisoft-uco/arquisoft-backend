package com.arquisoft.artefactos.application.versionartefacto.command.finder.impl;

import com.arquisoft.artefactos.application.versionartefacto.command.finder.CargaPendienteFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.mapper.CargaPendienteMapper;
import com.arquisoft.artefactos.domain.versionartefacto.CargaPendienteDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CargaPendienteFinderImpl implements CargaPendienteFinder {

    private final AlmacenamientoVersionOutputPort almacenamientoVersionOutputPort;

    @Override
    public CargaPendienteDomain obtener(UUID carga) {
        return almacenamientoVersionOutputPort.obtenerCargaPendiente(carga)
                .map(CargaPendienteMapper::toDomain)
                .orElse(CargaPendienteDomain.VACIO);
    }
}
