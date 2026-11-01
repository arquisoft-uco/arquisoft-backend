package com.arquisoft.artefactos.application.revisionasesor.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorRevisionAsesorFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantesVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionesDeRevisionExistenFinder;
import com.arquisoft.artefactos.application.observacion.command.finder.ObservacionesVersionAnteriorSinCerrarExistenFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionAsesorFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.secondaryport.RevisionAsesorOutputPort;
import com.arquisoft.artefactos.application.revisionasesor.command.usecase.CerrarRevisionAsesorUseCase;
import com.arquisoft.artefactos.application.revisionasesor.command.validator.CerrarRevisionAsesorValidator;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.VersionArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.revisionasesor.CierreRevisionAsesorDomain;
import com.arquisoft.artefactos.domain.revisionasesor.event.RevisionAsesorCerradaEvent;
import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ContactoEstudiante;
import com.arquisoft.artefactos.domain.revisionasesor.model.EstadoRevisionAsesor;
import com.arquisoft.artefactos.domain.revisionasesor.model.ObservacionesParaCierre;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.CerrarRevisionAsesorKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CerrarRevisionAsesorUseCaseImpl implements CerrarRevisionAsesorUseCase {

    private final RevisionAsesorFinder revisionAsesorFinder;
    private final ArtefactoPorRevisionAsesorFinder artefactoPorRevisionAsesorFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VersionArtefactoPorIdFinder versionArtefactoPorIdFinder;
    private final VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    private final EstudiantesVinculadosArtefactoFinder estudiantesVinculadosArtefactoFinder;
    private final ObservacionesDeRevisionExistenFinder observacionesDeRevisionExistenFinder;
    private final ObservacionesVersionAnteriorSinCerrarExistenFinder observacionesVersionAnteriorSinCerrarExistenFinder;
    private final CerrarRevisionAsesorValidator cerrarRevisionAsesorValidator;
    private final RevisionAsesorOutputPort revisionAsesorOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CierreRevisionAsesorDomain cierre) {
        var revisionAsesor = cierre.getRevisionAsesor();

        logger.info(CerrarRevisionAsesorKey.LOG_CERRANDO, revisionAsesor, cierre.getAsesor());

        var revision = revisionAsesorFinder.obtener(revisionAsesor);
        var artefacto = artefactoPorRevisionAsesorFinder.obtener(revisionAsesor);
        var asesor = asesorPorIdFinder.obtener(cierre.getAsesor());
        var version = versionArtefactoPorIdFinder.obtener(revision.getVersionArtefacto());
        var versionRepositorio = versionRepositorioArtefactoPorIdFinder.obtener(
                artefacto.getVersionRepositorioArtefacto());
        var estudiantes = estudiantesVinculadosArtefactoFinder.obtener(artefacto.getId());
        var existenPropias = observacionesDeRevisionExistenFinder.obtener(revisionAsesor);
        var existenSinCerrarVersionAnterior = observacionesVersionAnteriorSinCerrarExistenFinder.obtener(revisionAsesor);

        logger.debug(CerrarRevisionAsesorKey.LOG_VERIFICACION_CIERRE,
                !revision.esVacio(), !artefacto.esVacio(), !asesor.esVacio(), !version.esVacio(),
                !versionRepositorio.esVacio(), estudiantes.size(), existenPropias, existenSinCerrarVersionAnterior);

        cerrarRevisionAsesorValidator.validar(cierre, revision, artefacto, asesor, version, versionRepositorio,
                new ObservacionesParaCierre(existenPropias, existenSinCerrarVersionAnterior));

        revisionAsesorOutputPort.actualizarEstado(revisionAsesor, EstadoRevisionAsesor.CERRADA.getId());

        eventPublisher.publish(new RevisionAsesorCerradaEvent(
                versionRepositorio.getNombreDocumento(),
                version.getNumeroVersion(),
                new ContactoAsesor(asesor.getNombre(), asesor.getEmail()),
                estudiantes.stream()
                        .map(estudiante -> new ContactoEstudiante(estudiante.getNombre(), estudiante.getEmail()))
                        .toList()));

        logger.info(CerrarRevisionAsesorKey.LOG_CERRADA, revisionAsesor, estudiantes.size());
    }
}
