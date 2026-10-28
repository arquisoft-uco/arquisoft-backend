package com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudiante.command.finder.EstudiantesPorIdsFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudiantesPertenenciaProyectoGradoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudiantesYaVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.EstudianteArtefactoOutputPort;
import com.arquisoft.artefactos.application.estudianteartefacto.command.secondaryport.mapper.EstudianteArtefactoMapper;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.AsignarEstudiantesArtefactoUseCase;
import com.arquisoft.artefactos.application.estudianteartefacto.command.validator.AsignarEstudiantesArtefactoValidator;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.event.EstudiantesArtefactoAsignadosEvent;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.ContactoEstudiante;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EstudianteArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsignarEstudiantesArtefactoUseCaseImpl implements AsignarEstudiantesArtefactoUseCase {

    private final ArtefactoPorIdFinder artefactoPorIdFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    private final EstudiantesPorIdsFinder estudiantesPorIdsFinder;
    private final EstudiantesPertenenciaProyectoGradoFinder estudiantesPertenenciaProyectoGradoFinder;
    private final EstudiantesYaVinculadosArtefactoFinder estudiantesYaVinculadosArtefactoFinder;
    private final AsignarEstudiantesArtefactoValidator asignarEstudiantesArtefactoValidator;
    private final EstudianteArtefactoOutputPort estudianteArtefactoOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(AgregacionEstudiantesArtefactoDomain entrada) {
        logger.info(EstudianteArtefactoKey.LOG_ASIGNANDO, entrada.getAsesor(), entrada.getCantidad());

        var artefacto = artefactoPorIdFinder.obtener(entrada.getArtefacto());
        var asesor = asesorPorIdFinder.obtener(entrada.getAsesor());
        var versionRepositorio = versionRepositorioArtefactoPorIdFinder.obtener(
                artefacto.getVersionRepositorioArtefacto());
        var estudiantes = estudiantesPorIdsFinder.obtener(entrada.getEstudiantes());
        var pertenencias = estudiantesPertenenciaProyectoGradoFinder.obtener(entrada);
        var yaVinculados = estudiantesYaVinculadosArtefactoFinder.obtener(entrada);
        logger.debug(EstudianteArtefactoKey.LOG_VERIFICACION_ASIGNAR,
                !artefacto.esVacio(), !asesor.esVacio(), !versionRepositorio.esVacio(), estudiantes.size(),
                pertenencias.size(), yaVinculados.size());

        asignarEstudiantesArtefactoValidator.validar(
                entrada, artefacto, asesor, versionRepositorio, estudiantes, pertenencias, yaVinculados);

        estudianteArtefactoOutputPort.vincularTodos(entrada.getRelaciones().stream()
                .map(EstudianteArtefactoMapper::toEntity)
                .toList());

        eventPublisher.publish(new EstudiantesArtefactoAsignadosEvent(
                versionRepositorio.getNombreDocumento(),
                estudiantes.stream()
                        .map(estudiante -> new ContactoEstudiante(estudiante.getNombre(), estudiante.getEmail()))
                        .toList(),
                new ContactoAsesor(asesor.getNombre(), asesor.getEmail())));

        logger.info(EstudianteArtefactoKey.LOG_ASIGNADOS,
                entrada.getArtefacto(), entrada.getEstudiantes().size());
    }
}
