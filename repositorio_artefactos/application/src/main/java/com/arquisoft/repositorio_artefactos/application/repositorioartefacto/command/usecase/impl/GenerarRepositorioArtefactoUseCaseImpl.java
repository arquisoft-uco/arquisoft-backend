package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.NombreRepositorioArtefactoExisteFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.mapper.RepositorioArtefactoMapper;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.usecase.GenerarRepositorioArtefactoUseCase;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.validator.GenerarRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.RepositorioArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.RepositorioArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class GenerarRepositorioArtefactoUseCaseImpl implements GenerarRepositorioArtefactoUseCase {

    private final AdministradorPorIdFinder administradorPorIdFinder;
    private final NombreRepositorioArtefactoExisteFinder nombreRepositorioArtefactoExisteFinder;
    private final GenerarRepositorioArtefactoValidator generarRepositorioArtefactoValidator;
    private final RepositorioArtefactoOutputPort repositorioArtefactoOutputPort;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(RepositorioArtefactoDomain repositorio) {
        logger.info(RepositorioArtefactoKey.LOG_GENERANDO, repositorio.getAdministrador());

        var administrador = administradorPorIdFinder.obtener(repositorio.getAdministrador());
        var yaExiste = nombreRepositorioArtefactoExisteFinder.obtener(repositorio.getNombreRepositorio());
        logger.debug(RepositorioArtefactoKey.LOG_VERIFICACION_GENERAR, !administrador.esVacio(), yaExiste);

        generarRepositorioArtefactoValidator.validar(repositorio, administrador, yaExiste);

        repositorioArtefactoOutputPort.registrar(RepositorioArtefactoMapper.toEntity(repositorio));

        logger.info(RepositorioArtefactoKey.LOG_GENERADO, repositorio.getId());
        return repositorio.getId();
    }
}
