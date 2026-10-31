package com.arquisoft.artefactos.application.observacion.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorObservacionFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantesVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.DescripcionObservacionEnRevisionExisteFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionPorIdFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.model.DescripcionEnRevision;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.ObservacionOutputPort;
import com.arquisoft.artefactos.application.observacion.command.secondaryport.mapper.ObservacionMapper;
import com.arquisoft.artefactos.application.observacion.command.usecase.CambiarEstadoAsesorObservacionUseCase;
import com.arquisoft.artefactos.application.observacion.command.validator.CambiarEstadoAsesorObservacionValidator;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorPorObservacionFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionVersionSiguientePorObservacionFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.UltimoNumeroVersionArtefactoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.observacion.CambioEstadoAsesorObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.ObservacionDomain;
import com.arquisoft.artefactos.domain.observacion.event.ObservacionReabiertaEvent;
import com.arquisoft.artefactos.domain.observacion.model.VersionesDeObservacion;
import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoEstudiante;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.CambiarEstadoAsesorObservacionKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarEstadoAsesorObservacionUseCaseImpl implements CambiarEstadoAsesorObservacionUseCase {

    private final RevisionAsesorPorObservacionFinder revisionAsesorPorObservacionFinder;
    private final ObservacionPorIdFinder observacionPorIdFinder;
    private final ArtefactoPorObservacionFinder artefactoPorObservacionFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VersionArtefactoPorIdFinder versionArtefactoPorIdFinder;
    private final VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    private final EstudiantesVinculadosArtefactoFinder estudiantesVinculadosArtefactoFinder;
    private final UltimoNumeroVersionArtefactoFinder ultimoNumeroVersionArtefactoFinder;
    private final RevisionVersionSiguientePorObservacionFinder revisionVersionSiguientePorObservacionFinder;
    private final DescripcionObservacionEnRevisionExisteFinder descripcionObservacionEnRevisionExisteFinder;
    private final CambiarEstadoAsesorObservacionValidator cambiarEstadoAsesorObservacionValidator;
    private final ObservacionOutputPort observacionOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioEstadoAsesorObservacionDomain cambio) {
        var observacionId = cambio.getObservacion();

        logger.info(CambiarEstadoAsesorObservacionKey.LOG_CAMBIANDO_ESTADO,
                observacionId, cambio.getAsesor(), cambio.getEstado().getId());

        var revision = revisionAsesorPorObservacionFinder.obtener(observacionId);
        var observacion = observacionPorIdFinder.obtener(observacionId);
        var artefacto = artefactoPorObservacionFinder.obtener(observacionId);
        var asesor = asesorPorIdFinder.obtener(cambio.getAsesor());
        var version = versionArtefactoPorIdFinder.obtener(revision.getVersionArtefacto());
        var versionRepositorio = versionRepositorioArtefactoPorIdFinder.obtener(
                artefacto.getVersionRepositorioArtefacto());
        var estudiantes = estudiantesVinculadosArtefactoFinder.obtener(artefacto.getId());
        var ultimoNumeroVersion = ultimoNumeroVersionArtefactoFinder.obtener(artefacto.getId());
        var revisionVersionSiguiente = revisionVersionSiguientePorObservacionFinder.obtener(observacionId);
        var descripcionRepetida = descripcionObservacionEnRevisionExisteFinder.obtener(
                new DescripcionEnRevision(revisionVersionSiguiente.getId(), observacion.getDescripcion()));
        var versiones = new VersionesDeObservacion(
                version, ultimoNumeroVersion, revisionVersionSiguiente, descripcionRepetida);

        logger.debug(CambiarEstadoAsesorObservacionKey.LOG_VERIFICACION_CAMBIO_ESTADO,
                !observacion.esVacio(), !revision.esVacio(), !artefacto.esVacio(), !asesor.esVacio(),
                !version.esVacio(), !versionRepositorio.esVacio(), estudiantes.size(),
                ultimoNumeroVersion, !revisionVersionSiguiente.esVacio(), descripcionRepetida);

        cambiarEstadoAsesorObservacionValidator.validar(
                cambio, observacion, revision, artefacto, asesor, versionRepositorio, versiones);

        observacionOutputPort.actualizarEstado(observacionId, cambio.getEstado().getId());

        if (cambio.getEstado().esReabierta()) {
            var continuacion = ObservacionDomain.crearContinuacion(observacion, revisionVersionSiguiente.getId());
            observacionOutputPort.registrar(ObservacionMapper.toEntity(continuacion));
            eventPublisher.publish(new ObservacionReabiertaEvent(
                    versionRepositorio.getNombreDocumento(),
                    version.getNumeroVersion(),
                    new ContactoAsesor(asesor.getNombre(), asesor.getEmail()),
                    estudiantes.stream()
                            .map(estudiante -> new ContactoEstudiante(estudiante.getNombre(), estudiante.getEmail()))
                            .toList(),
                    observacion.getDescripcion()));
        }

        logger.info(CambiarEstadoAsesorObservacionKey.LOG_ESTADO_CAMBIADO,
                observacionId, cambio.getEstado().getId());
    }
}
