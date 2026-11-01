package com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantePorIdFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudianteArtefactoExisteFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.model.ParticipacionEstudianteArtefacto;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.RemoverEstudianteArtefactoUseCase;
import com.arquisoft.artefactos.application.estudianteartefacto.command.validator.RemoverEstudianteArtefactoValidator;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.estudianteartefacto.RemocionEstudianteArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.event.EstudianteArtefactoRemovidoEvent;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoEstudiante;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteArtefactoUseCaseImpl implements RemoverEstudianteArtefactoUseCase {

    private final ArtefactoPorIdFinder artefactoPorIdFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    private final EstudiantePorIdFinder estudiantePorIdFinder;
    private final EstudianteArtefactoExisteFinder estudianteArtefactoExisteFinder;
    private final RemoverEstudianteArtefactoValidator removerEstudianteArtefactoValidator;
    private final EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionEstudianteArtefactoDomain entrada) {
        logger.info(EstudianteArtefactoKey.LOG_REMOVIENDO,
                entrada.getArtefacto(), entrada.getAsesor(), entrada.getEstudiante());

        var artefacto = artefactoPorIdFinder.obtener(entrada.getArtefacto());
        var asesor = asesorPorIdFinder.obtener(entrada.getAsesor());
        var versionRepositorio = versionRepositorioArtefactoPorIdFinder.obtener(
                artefacto.getVersionRepositorioArtefacto());
        var estudiante = estudiantePorIdFinder.obtener(entrada.getEstudiante());
        var vinculado = estudianteArtefactoExisteFinder.obtener(
                new ParticipacionEstudianteArtefacto(entrada.getEstudiante(), entrada.getArtefacto()));
        logger.debug(EstudianteArtefactoKey.LOG_VERIFICACION_REMOVER,
                !artefacto.esVacio(), !asesor.esVacio(), !versionRepositorio.esVacio(), !estudiante.esVacio(),
                vinculado);

        removerEstudianteArtefactoValidator.validar(
                entrada, artefacto, asesor, versionRepositorio, estudiante, vinculado);

        estudianteArtefactoOutputPort.eliminarPorArtefactoYEstudiante(
                entrada.getArtefacto(), entrada.getEstudiante());

        eventPublisher.publish(new EstudianteArtefactoRemovidoEvent(
                versionRepositorio.getNombreDocumento(),
                new ContactoEstudiante(estudiante.getNombre(), estudiante.getEmail()),
                new ContactoAsesor(asesor.getNombre(), asesor.getEmail())));

        logger.info(EstudianteArtefactoKey.LOG_REMOVIDO, entrada.getArtefacto(), entrada.getEstudiante());
    }
}
