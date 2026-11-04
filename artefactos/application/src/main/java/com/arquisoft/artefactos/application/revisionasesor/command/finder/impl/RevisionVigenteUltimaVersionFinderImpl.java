package com.arquisoft.artefactos.application.revisionasesor.command.finder.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionVigenteUltimaVersionFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.mapper.RevisionAsesorMapper;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevisionVigenteUltimaVersionFinderImpl implements RevisionVigenteUltimaVersionFinder {

    private final RevisionAsesorOutputPort revisionAsesorOutputPort;

    @Override
    public RevisionAsesorDomain obtener(UUID artefacto) {
        return revisionAsesorOutputPort.obtenerMasRecienteDeUltimaVersion(artefacto)
                .map(RevisionAsesorMapper::toDomain)
                .orElse(RevisionAsesorDomain.VACIO);
    }
}
