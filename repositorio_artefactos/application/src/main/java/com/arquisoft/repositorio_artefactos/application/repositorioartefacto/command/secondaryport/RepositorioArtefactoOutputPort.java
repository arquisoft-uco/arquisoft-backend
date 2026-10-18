package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.entity.RepositorioArtefactoEntity;

import java.util.Optional;
import java.util.UUID;

public interface RepositorioArtefactoOutputPort {

    void registrar(RepositorioArtefactoEntity repositorio);

    boolean existePorNombreRepositorio(String nombreRepositorio);

    Optional<RepositorioArtefactoEntity> obtenerPorId(UUID id);
}
