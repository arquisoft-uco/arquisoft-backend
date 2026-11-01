package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoBloqueadoPorIdFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.usecase.CambiarVersionRepositorioArtefactoUseCase;
import com.arquisoft.artefactos.application.artefacto.command.validator.CambiarVersionRepositorioArtefactoValidator;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantesVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionVigenteUltimaVersionFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.UltimoNumeroVersionArtefactoFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorArtefactoFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.artefacto.CambioVersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.event.ArtefactoVersionRepositorioCambiadaEvent;
import com.arquisoft.artefactos.domain.artefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.artefacto.model.ContactoEstudiante;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarVersionRepositorioArtefactoUseCaseImpl implements CambiarVersionRepositorioArtefactoUseCase {

    private final ArtefactoBloqueadoPorIdFinder artefactoBloqueadoPorIdFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VersionRepositorioArtefactoPorArtefactoFinder versionRepositorioArtefactoPorArtefactoFinder;
    private final VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    private final UltimoNumeroVersionArtefactoFinder ultimoNumeroVersionArtefactoFinder;
    private final RevisionVigenteUltimaVersionFinder revisionVigenteUltimaVersionFinder;
    private final EstudiantesVinculadosArtefactoFinder estudiantesVinculadosArtefactoFinder;
    private final CambiarVersionRepositorioArtefactoValidator cambiarVersionRepositorioArtefactoValidator;
    private final ArtefactoOutputPort artefactoOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioVersionRepositorioArtefactoDomain cambio) {
        logger.info(ArtefactoKey.LOG_CAMBIANDO_VERSION_REPOSITORIO, cambio.getArtefacto(), cambio.getAsesor());

        var artefacto = artefactoBloqueadoPorIdFinder.obtener(cambio.getArtefacto());
        var asesor = asesorPorIdFinder.obtener(cambio.getAsesor());
        var actual = versionRepositorioArtefactoPorArtefactoFinder.obtener(cambio.getArtefacto());
        var elegida = versionRepositorioArtefactoPorIdFinder.obtener(cambio.getVersionRepositorioArtefacto());
        var ultimoNumero = ultimoNumeroVersionArtefactoFinder.obtener(cambio.getArtefacto());
        var revisionVigente = revisionVigenteUltimaVersionFinder.obtener(cambio.getArtefacto());
        var estudiantes = estudiantesVinculadosArtefactoFinder.obtener(cambio.getArtefacto());
        logger.debug(ArtefactoKey.LOG_VERIFICACION_CAMBIAR_VERSION_REPOSITORIO,
                !artefacto.esVacio(), !asesor.esVacio(), !actual.esVacio(), !elegida.esVacio(),
                ultimoNumero, revisionVigente.getEstado().getId(), estudiantes.size());

        cambiarVersionRepositorioArtefactoValidator.validar(
                cambio, artefacto, asesor, actual, elegida, new RevisionVersionAnterior(ultimoNumero, revisionVigente));

        artefacto.cambiarVersionRepositorio(cambio.getVersionRepositorioArtefacto());
        artefactoOutputPort.cambiarVersionRepositorio(artefacto.getId(), artefacto.getVersionRepositorioArtefacto());

        eventPublisher.publish(new ArtefactoVersionRepositorioCambiadaEvent(
                elegida.getNombreDocumento(),
                actual.getVersionDocumento(),
                elegida.getVersionDocumento(),
                estudiantes.stream()
                        .map(estudiante -> new ContactoEstudiante(estudiante.getNombre(), estudiante.getEmail()))
                        .toList(),
                new ContactoAsesor(asesor.getNombre(), asesor.getEmail())));

        logger.info(ArtefactoKey.LOG_VERSION_REPOSITORIO_CAMBIADA,
                cambio.getArtefacto(), cambio.getVersionRepositorioArtefacto(), estudiantes.size());
    }
}
