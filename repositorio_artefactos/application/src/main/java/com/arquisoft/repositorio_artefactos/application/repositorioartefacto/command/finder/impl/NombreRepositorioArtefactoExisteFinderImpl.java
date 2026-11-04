package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.NombreRepositorioArtefactoExisteFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NombreRepositorioArtefactoExisteFinderImpl implements NombreRepositorioArtefactoExisteFinder {

    private final RepositorioArtefactoOutputPort repositorioArtefactoOutputPort;

    @Override
    public Boolean obtener(String nombreRepositorio) {
        return repositorioArtefactoOutputPort.existePorNombreRepositorio(nombreRepositorio);
    }
}
