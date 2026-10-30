package com.arquisoft.artefactos.infrastructure.artefacto.command.primaryadapter.web.mapper;

import com.arquisoft.artefactos.application.artefacto.command.primaryport.model.OmitirArtefactoCommand;

import java.util.UUID;

public final class OmitirArtefactoRequestMapper {

    private OmitirArtefactoRequestMapper() {}

    public static OmitirArtefactoCommand toCommand(UUID artefacto, UUID asesor) {
        return OmitirArtefactoCommand.crear(artefacto, asesor);
    }
}
