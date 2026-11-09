package com.arquisoft.artefactos.application.revisionasesor.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorVersionArtefactoFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorVersionArtefactoFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.ProyectoGradoNoCongeladoValidator;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorAbiertaPorVersionFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.mapper.RevisionAsesorMapper;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.RegistrarRevisionAsesorUseCase;
import com.arquisoft.artefactos.application.revisionasesor.command.validator.RegistrarRevisionAsesorValidator;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoEsUltimaFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoBloqueadaPorIdFinder;
import com.arquisoft.artefactos.domain.revisionasesor.RegistroRevisionAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import com.arquisoft.shared.message.key.artefactos.RegistrarRevisionAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarRevisionAsesorUseCaseImpl implements RegistrarRevisionAsesorUseCase {

    private final VersionArtefactoBloqueadaPorIdFinder versionArtefactoBloqueadaPorIdFinder;
    private final ArtefactoPorVersionArtefactoFinder artefactoPorVersionArtefactoFinder;
    private final VersionArtefactoEsUltimaFinder versionArtefactoEsUltimaFinder;
    private final RevisionAsesorAbiertaPorVersionFinder revisionAsesorAbiertaPorVersionFinder;
    private final RegistrarRevisionAsesorValidator registrarRevisionAsesorValidator;
    private final RevisionAsesorOutputPort revisionAsesorOutputPort;
    private final ProyectoGradoPorVersionArtefactoFinder proyectoGradoPorVersionArtefactoFinder;
    private final ProyectoGradoNoCongeladoValidator proyectoGradoNoCongeladoValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(RegistroRevisionAsesorDomain registro) {
        var revision = registro.getRevision();
        var versionArtefacto = revision.getVersionArtefacto();

        logger.info(RegistrarRevisionAsesorKey.LOG_REGISTRANDO, versionArtefacto, registro.getAsesor());

        var version = versionArtefactoBloqueadaPorIdFinder.obtener(versionArtefacto);
        var artefacto = artefactoPorVersionArtefactoFinder.obtener(versionArtefacto);
        var esUltima = versionArtefactoEsUltimaFinder.obtener(versionArtefacto);
        var existeAbierta = revisionAsesorAbiertaPorVersionFinder.obtener(versionArtefacto);
        var proyecto = proyectoGradoPorVersionArtefactoFinder.obtener(versionArtefacto);
        logger.debug(RegistrarRevisionAsesorKey.LOG_VERIFICACION_REGISTRO,
                !version.esVacio(), !artefacto.esVacio(), esUltima, existeAbierta);

        registrarRevisionAsesorValidator.validar(registro, version, artefacto, esUltima, existeAbierta);

        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_CONGELAMIENTO, !proyecto.esVacio(), proyecto.estaCongelado());
        proyectoGradoNoCongeladoValidator.validar(proyecto);

        revisionAsesorOutputPort.registrar(RevisionAsesorMapper.toEntity(revision));

        logger.info(RegistrarRevisionAsesorKey.LOG_REGISTRADA, revision.getId(), version.getNumeroVersion());
        return revision.getId();
    }
}
