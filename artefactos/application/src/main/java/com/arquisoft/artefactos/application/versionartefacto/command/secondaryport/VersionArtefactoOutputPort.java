package com.arquisoft.artefactos.application.versionartefacto.command.secondaryport;

import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.entity.VersionArtefactoEntity;

import java.util.UUID;

public interface VersionArtefactoOutputPort {

    void registrar(VersionArtefactoEntity version);

    int ultimoNumeroPorArtefacto(UUID artefacto);
}
