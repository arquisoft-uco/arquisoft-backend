package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.entity.RepositorioArtefactoEntity;

public interface RepositorioArtefactoOutputPort {

    void registrar(RepositorioArtefactoEntity repositorio);

    boolean existePorNombreRepositorio(String nombreRepositorio);
}
