package com.arquisoft.artefactos.application.versionartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoBloqueadoPorVersionArtefactoFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorArtefactoFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.ProyectoGradoNoCongeladoValidator;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAnteriorBloqueadaDeVersionFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionDeVersionExisteFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.EstudianteParticipaVersionArtefactoExisteFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoBloqueadaPorIdFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoEsUltimaFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.usecase.EliminarVersionArtefactoUseCase;
import com.arquisoft.artefactos.application.versionartefacto.command.validator.EliminarVersionArtefactoValidator;
import com.arquisoft.artefactos.domain.versionartefacto.EliminacionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.PrecondicionesEliminacionVersion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EliminarVersionArtefactoKey;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarVersionArtefactoUseCaseImpl implements EliminarVersionArtefactoUseCase {

    private final ArtefactoBloqueadoPorVersionArtefactoFinder artefactoBloqueadoPorVersionArtefactoFinder;
    private final RevisionAnteriorBloqueadaDeVersionFinder revisionAnteriorBloqueadaDeVersionFinder;
    private final VersionArtefactoBloqueadaPorIdFinder versionArtefactoBloqueadaPorIdFinder;
    private final EstudianteParticipaVersionArtefactoExisteFinder estudianteParticipaVersionArtefactoExisteFinder;
    private final VersionArtefactoEsUltimaFinder versionArtefactoEsUltimaFinder;
    private final RevisionDeVersionExisteFinder revisionDeVersionExisteFinder;
    private final EliminarVersionArtefactoValidator eliminarVersionArtefactoValidator;
    private final AlmacenamientoVersionOutputPort almacenamientoVersionOutputPort;
    private final VersionArtefactoOutputPort versionArtefactoOutputPort;
    private final ProyectoGradoBloqueadoPorArtefactoFinder proyectoGradoBloqueadoPorArtefactoFinder;
    private final ProyectoGradoNoCongeladoValidator proyectoGradoNoCongeladoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionVersionArtefactoDomain eliminacion) {
        var versionArtefacto = eliminacion.getVersionArtefacto();

        logger.info(EliminarVersionArtefactoKey.LOG_ELIMINANDO, versionArtefacto, eliminacion.getSolicitante());

        var artefacto = artefactoBloqueadoPorVersionArtefactoFinder.obtener(versionArtefacto);
        var revisionAnterior = revisionAnteriorBloqueadaDeVersionFinder.obtener(versionArtefacto);
        var version = versionArtefactoBloqueadaPorIdFinder.obtener(versionArtefacto);
        var participa = estudianteParticipaVersionArtefactoExisteFinder.obtener(eliminacion);
        var esUltima = versionArtefactoEsUltimaFinder.obtener(versionArtefacto);
        var tieneRevision = revisionDeVersionExisteFinder.obtener(versionArtefacto);

        var proyecto = proyectoGradoBloqueadoPorArtefactoFinder.obtener(artefacto.getId());
        logger.debug(EliminarVersionArtefactoKey.LOG_VERIFICACION_ELIMINAR,
                !version.esVacio(), participa, esUltima, tieneRevision, revisionAnterior.getEstado().getId());

        eliminarVersionArtefactoValidator.validar(eliminacion, new PrecondicionesEliminacionVersion(
                version, artefacto, revisionAnterior, participa, esUltima, tieneRevision));

        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_CONGELAMIENTO, !proyecto.esVacio(), proyecto.estaCongelado());
        proyectoGradoNoCongeladoValidator.validar(proyecto);

        almacenamientoVersionOutputPort.eliminarArchivo(version.getUriArchivo());
        versionArtefactoOutputPort.eliminar(version.getId());

        logger.info(EliminarVersionArtefactoKey.LOG_ELIMINADA,
                version.getId(), version.getNumeroVersion(), version.getArtefacto());
    }
}
