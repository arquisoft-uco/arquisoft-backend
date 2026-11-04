package com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.NombreEnOtroRepositorioExisteFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.RepositorioArtefactoPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.secondaryport.RepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.usecase.ModificarRepositorioArtefactoUseCase;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.validator.ModificarRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.ModificacionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.repositorioartefacto.event.RepositorioArtefactoModificadoEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.RepositorioArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarRepositorioArtefactoUseCaseImpl implements ModificarRepositorioArtefactoUseCase {

    private final AdministradorPorIdFinder administradorPorIdFinder;
    private final RepositorioArtefactoPorIdFinder repositorioArtefactoPorIdFinder;
    private final NombreEnOtroRepositorioExisteFinder nombreEnOtroRepositorioExisteFinder;
    private final ModificarRepositorioArtefactoValidator modificarRepositorioArtefactoValidator;
    private final RepositorioArtefactoOutputPort repositorioArtefactoOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionRepositorioArtefactoDomain modificacion) {
        logger.info(RepositorioArtefactoKey.LOG_MODIFICANDO,
                modificacion.getRepositorio(), modificacion.getAdministrador());

        var administrador = administradorPorIdFinder.obtener(modificacion.getAdministrador());
        var repositorio = repositorioArtefactoPorIdFinder.obtener(modificacion.getRepositorio());
        var nombreEnOtroRepositorio = nombreEnOtroRepositorioExisteFinder.obtener(modificacion);
        logger.debug(RepositorioArtefactoKey.LOG_VERIFICACION_MODIFICAR,
                !administrador.esVacio(), !repositorio.esVacio(), nombreEnOtroRepositorio);

        modificarRepositorioArtefactoValidator.validar(
                modificacion, administrador, repositorio, nombreEnOtroRepositorio);

        repositorioArtefactoOutputPort.actualizarDatos(
                modificacion.getRepositorio(), modificacion.getNombreRepositorio(), modificacion.getDescripcion());
        eventPublisher.publish(new RepositorioArtefactoModificadoEvent(modificacion));

        logger.info(RepositorioArtefactoKey.LOG_MODIFICADO, modificacion.getRepositorio());
    }
}
