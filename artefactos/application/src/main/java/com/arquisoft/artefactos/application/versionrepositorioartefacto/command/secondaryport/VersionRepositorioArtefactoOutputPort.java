package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport;

import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.entity.VersionRepositorioArtefactoEntity;

import java.util.UUID;

public interface VersionRepositorioArtefactoOutputPort {

    void guardar(VersionRepositorioArtefactoEntity version);

    boolean existePorId(UUID id);

    void actualizarNombreRepositorio(UUID repositorioArtefacto, String nombreRepositorio);
}
