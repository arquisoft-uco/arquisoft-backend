package com.arquisoft.artefactos.application.revisionasesor.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorRevisionAsesorFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorDeRevisionAsesorFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantePorIdFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionesAbiertasDeRevisionExistenFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorRevisionAsesorFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.ProyectoGradoNoCongeladoValidator;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.EstudianteParticipaRevisionAsesorFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.VersionPosteriorARevisionFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.CambiarEstadoRevisionAsesorUseCase;
import com.arquisoft.artefactos.application.revisionasesor.command.validator.CambiarEstadoRevisionAsesorValidator;
import com.arquisoft.artefactos.domain.revisionasesor.CambioEstadoRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.event.RevisionAsesorEstadoCambiadoEvent;
import com.arquisoft.artefactos.domain.revisionasesor.model.PrecondicionesCambioEstadoRevisionAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.CambiarEstadoRevisionAsesorKey;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarEstadoRevisionAsesorUseCaseImpl implements CambiarEstadoRevisionAsesorUseCase {

    private final RevisionAsesorOutputPort revisionAsesorOutputPort;
    private final RevisionAsesorFinder revisionAsesorFinder;
    private final AsesorDeRevisionAsesorFinder asesorDeRevisionAsesorFinder;
    private final EstudiantePorIdFinder estudiantePorIdFinder;
    private final EstudianteParticipaRevisionAsesorFinder estudianteParticipaRevisionAsesorFinder;
    private final VersionPosteriorARevisionFinder versionPosteriorARevisionFinder;
    private final ArtefactoPorRevisionAsesorFinder artefactoPorRevisionAsesorFinder;
    private final ObservacionesAbiertasDeRevisionExistenFinder observacionesAbiertasDeRevisionExistenFinder;
    private final CambiarEstadoRevisionAsesorValidator cambiarEstadoRevisionAsesorValidator;
    private final EventPublisher eventPublisher;
    private final ProyectoGradoPorRevisionAsesorFinder proyectoGradoPorRevisionAsesorFinder;
    private final ProyectoGradoNoCongeladoValidator proyectoGradoNoCongeladoValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioEstadoRevisionAsesorDomain cambio) {
        var revisionAsesor = cambio.getRevisionAsesor();
        var estadoNuevo = cambio.getEstado();

        logger.info(CambiarEstadoRevisionAsesorKey.LOG_CAMBIANDO_ESTADO, revisionAsesor, estadoNuevo.getId());

        var revision = revisionAsesorFinder.obtener(revisionAsesor);
        var asesor = asesorDeRevisionAsesorFinder.obtener(revisionAsesor);
        var estudiante = estudiantePorIdFinder.obtener(cambio.getEstudiante());
        var participa = estudianteParticipaRevisionAsesorFinder.obtener(cambio);
        var existeVersionPosterior = versionPosteriorARevisionFinder.obtener(revisionAsesor);
        var artefacto = artefactoPorRevisionAsesorFinder.obtener(revisionAsesor);
        var existenObservacionesAbiertas = observacionesAbiertasDeRevisionExistenFinder.obtener(revisionAsesor);

        var proyecto = proyectoGradoPorRevisionAsesorFinder.obtener(revisionAsesor);
        logger.debug(CambiarEstadoRevisionAsesorKey.LOG_VERIFICACION_CAMBIO_ESTADO,
                !revision.esVacio(), !asesor.esVacio(), !estudiante.esVacio(), participa, existeVersionPosterior,
                !artefacto.esVacio(), existenObservacionesAbiertas);

        cambiarEstadoRevisionAsesorValidator.validar(cambio, new PrecondicionesCambioEstadoRevisionAsesor(
                revision, asesor, estudiante, artefacto, participa, existeVersionPosterior,
                existenObservacionesAbiertas));

        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_CONGELAMIENTO, !proyecto.esVacio(), proyecto.estaCongelado());
        proyectoGradoNoCongeladoValidator.validar(proyecto);

        if (revision.tieneEstado(estadoNuevo)) {
            logger.info(CambiarEstadoRevisionAsesorKey.LOG_ESTADO_SIN_CAMBIO, revisionAsesor, estadoNuevo.getId());
            return;
        }

        revisionAsesorOutputPort.actualizarEstado(revisionAsesor, estadoNuevo.getId());

        if (estadoNuevo.esResuelta()) {
            eventPublisher.publish(new RevisionAsesorEstadoCambiadoEvent(revisionAsesor, estadoNuevo.getId(),
                    asesor.getNombre(), asesor.getEmail(), estudiante.getNombre()));
        }

        logger.info(CambiarEstadoRevisionAsesorKey.LOG_ESTADO_CAMBIADO, revisionAsesor, estadoNuevo.getId());
    }
}
