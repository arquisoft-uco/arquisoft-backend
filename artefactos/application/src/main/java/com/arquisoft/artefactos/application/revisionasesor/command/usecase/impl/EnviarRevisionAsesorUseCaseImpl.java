package com.arquisoft.artefactos.application.revisionasesor.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorRevisionAsesorFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantesVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.observacion.command.usecase.AvanzarObservacionesRevisionUseCase;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.EnviarRevisionAsesorUseCase;
import com.arquisoft.artefactos.application.revisionasesor.command.validator.EnviarRevisionAsesorValidator;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.revisionasesor.EnvioRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.event.RevisionAsesorEnviadaEvent;
import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoEstudiante;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EnviarRevisionAsesorKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EnviarRevisionAsesorUseCaseImpl implements EnviarRevisionAsesorUseCase {

    private final RevisionAsesorFinder revisionAsesorFinder;
    private final ArtefactoPorRevisionAsesorFinder artefactoPorRevisionAsesorFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VersionArtefactoPorIdFinder versionArtefactoPorIdFinder;
    private final VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    private final EstudiantesVinculadosArtefactoFinder estudiantesVinculadosArtefactoFinder;
    private final EnviarRevisionAsesorValidator enviarRevisionAsesorValidator;
    private final RevisionAsesorOutputPort revisionAsesorOutputPort;
    private final AvanzarObservacionesRevisionUseCase avanzarObservacionesRevisionUseCase;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(EnvioRevisionAsesorDomain envio) {
        var revisionAsesor = envio.getRevisionAsesor();

        logger.info(EnviarRevisionAsesorKey.LOG_ENVIANDO, revisionAsesor, envio.getAsesor());

        var revision = revisionAsesorFinder.obtener(revisionAsesor);
        var artefacto = artefactoPorRevisionAsesorFinder.obtener(revisionAsesor);
        var asesor = asesorPorIdFinder.obtener(envio.getAsesor());
        var version = versionArtefactoPorIdFinder.obtener(revision.getVersionArtefacto());
        var versionRepositorio = versionRepositorioArtefactoPorIdFinder.obtener(
                artefacto.getVersionRepositorioArtefacto());
        var estudiantes = estudiantesVinculadosArtefactoFinder.obtener(artefacto.getId());

        logger.debug(EnviarRevisionAsesorKey.LOG_VERIFICACION_ENVIO,
                !revision.esVacio(), !artefacto.esVacio(), !asesor.esVacio(), !version.esVacio(),
                !versionRepositorio.esVacio(), estudiantes.size());

        enviarRevisionAsesorValidator.validar(envio, revision, artefacto, asesor, version, versionRepositorio);

        revisionAsesorOutputPort.actualizarEstado(revisionAsesor, EstadoRevisionAsesor.EN_PROGRESO.getId());
        avanzarObservacionesRevisionUseCase.ejecutar(envio.getObservaciones());

        eventPublisher.publish(new RevisionAsesorEnviadaEvent(
                versionRepositorio.getNombreDocumento(),
                version.getNumeroVersion(),
                new ContactoAsesor(asesor.getNombre(), asesor.getEmail()),
                estudiantes.stream()
                        .map(estudiante -> new ContactoEstudiante(estudiante.getNombre(), estudiante.getEmail()))
                        .toList()));

        logger.info(EnviarRevisionAsesorKey.LOG_ENVIADA, revisionAsesor, estudiantes.size());
    }
}
