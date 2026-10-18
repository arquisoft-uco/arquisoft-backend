package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;

import java.util.UUID;

public interface VersionRepositorioArtefactoOutputPort {

    void registrar(VersionRepositorioArtefactoEntity version);

    int obtenerUltimoNumeroVersion(UUID repositorio, String nombreDocumento);
}
