package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.CargaPendienteFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper.CargaPendienteMapper;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.CargaPendienteDomain;
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
