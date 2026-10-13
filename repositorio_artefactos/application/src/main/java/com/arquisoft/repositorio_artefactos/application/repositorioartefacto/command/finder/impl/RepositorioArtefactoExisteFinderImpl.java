package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.impl;

import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.RepositorioArtefactoExisteFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RepositorioArtefactoExisteFinderImpl implements RepositorioArtefactoExisteFinder {

    private final RepositorioArtefactoOutputPort repositorioArtefactoOutputPort;

    @Override
    public Boolean obtener(RepositorioArtefactoDomain repositorio) {
        return repositorioArtefactoOutputPort.existePorNombreDescripcionYAdministrador(
                repositorio.getNombreRepositorio(),
                repositorio.getDescripcion(),
                repositorio.getAdministrador());
    }
}
