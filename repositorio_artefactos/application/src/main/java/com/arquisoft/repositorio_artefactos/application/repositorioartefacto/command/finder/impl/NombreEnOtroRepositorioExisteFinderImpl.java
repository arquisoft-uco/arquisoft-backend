package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.NombreEnOtroRepositorioExisteFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.ModificacionRepositorioArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NombreEnOtroRepositorioExisteFinderImpl implements NombreEnOtroRepositorioExisteFinder {

    private final RepositorioArtefactoOutputPort repositorioArtefactoOutputPort;

    @Override
    public Boolean obtener(ModificacionRepositorioArtefactoDomain modificacion) {
        return repositorioArtefactoOutputPort.existeOtroConNombreRepositorio(
                modificacion.getNombreRepositorio(), modificacion.getRepositorio());
    }
}
