package com.arquisoft.artefactos.application.revisionasesor.command.finder.impl;

import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.mapper.RevisionAsesorMapper;
import com.arquisoft.artefactos.domain.revisionasesor.RevisionAsesorDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RevisionAsesorFinderImpl implements RevisionAsesorFinder {

    private final RevisionAsesorOutputPort revisionAsesorOutputPort;

    @Override
    public RevisionAsesorDomain obtener(UUID id) {
        return revisionAsesorOutputPort.obtenerPorId(id)
                .map(RevisionAsesorMapper::toDomain)
                .orElse(RevisionAsesorDomain.VACIO);
    }
}
