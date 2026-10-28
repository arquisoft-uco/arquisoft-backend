package com.arquisoft.artefactos.application.asesor.command.finder.impl;

import com.arquisoft.artefactos.application.asesor.command.finder.AsesorDeRevisionAsesorFinder;
import com.arquisoft.artefactos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.artefactos.application.asesor.command.secondaryport.mapper.AsesorMapper;
import com.arquisoft.artefactos.domain.asesor.AsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorDeRevisionAsesorFinderImpl implements AsesorDeRevisionAsesorFinder {

    private final AsesorOutputPort asesorOutputPort;

    @Override
    public AsesorDomain obtener(UUID revisionAsesor) {
        return asesorOutputPort.obtenerPorRevisionAsesor(revisionAsesor)
                .map(AsesorMapper::toDomain)
                .orElse(AsesorDomain.VACIO);
    }
}
