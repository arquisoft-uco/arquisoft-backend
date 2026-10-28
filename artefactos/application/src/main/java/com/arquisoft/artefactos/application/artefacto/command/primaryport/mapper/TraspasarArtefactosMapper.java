package com.arquisoft.artefactos.application.artefacto.command.primaryport.mapper;

import com.arquisoft.artefactos.domain.artefacto.TraspasoArtefactosDomain;

import java.util.UUID;

public final class TraspasarArtefactosMapper {

    private TraspasarArtefactosMapper() {}

    public static TraspasoArtefactosDomain toDomain(UUID proyectoGrado, UUID asesor) {
        return TraspasoArtefactosDomain.crear(proyectoGrado, asesor);
    }
}
