package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.entity.RepositorioArtefactoEntity;

import java.util.Optional;
import java.util.UUID;

public interface RepositorioArtefactoOutputPort {

    void registrar(RepositorioArtefactoEntity repositorio);

    boolean existePorNombreRepositorio(String nombreRepositorio);

    boolean existeOtroConNombreRepositorio(String nombreRepositorio, UUID repositorio);

    Optional<RepositorioArtefactoEntity> obtenerPorId(UUID id);

    void actualizarDatos(UUID repositorio, String nombreRepositorio, String descripcion);
}
