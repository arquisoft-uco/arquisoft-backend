package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.RepositoriosAsignadosProyectoFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.mapper.ArtefactoMapper;
import com.arquisoft.artefactos.application.artefacto.command.usecase.AsignarArtefactoUseCase;
import com.arquisoft.artefactos.application.artefacto.command.validator.AsignarArtefactoValidator;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.VinculosMasRecientesProyectoGradoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.AsignarEstudiantesArtefactoUseCase;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionesRepositorioArtefactoPorIdsFinder;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarArtefactoUseCaseImpl implements AsignarArtefactoUseCase {

    private final ProyectoGradoExisteFinder proyectoGradoExisteFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VinculosMasRecientesProyectoGradoFinder vinculosMasRecientesProyectoGradoFinder;
    private final VersionesRepositorioArtefactoPorIdsFinder versionesRepositorioArtefactoPorIdsFinder;
    private final RepositoriosAsignadosProyectoFinder repositoriosAsignadosProyectoFinder;
    private final AsignarArtefactoValidator asignarArtefactoValidator;
    private final ArtefactoOutputPort artefactoOutputPort;
    private final AsignarEstudiantesArtefactoUseCase asignarEstudiantesArtefactoUseCase;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AsignacionArtefactoDomain registro) {
        var artefacto = registro.getArtefacto();
        logger.info(ArtefactoKey.LOG_ASIGNANDO, artefacto.getProyectoGrado(), artefacto.getAsesor());

        var proyectoExiste = proyectoGradoExisteFinder.obtener(artefacto.getProyectoGrado());
        var asesor = asesorPorIdFinder.obtener(artefacto.getAsesor());
        var vinculosMasRecientes = vinculosMasRecientesProyectoGradoFinder.obtener(artefacto.getProyectoGrado());
        var versiones = versionesRepositorioArtefactoPorIdsFinder.obtener(
                List.of(artefacto.getVersionRepositorioArtefacto()));
        var repositoriosAsignados = repositoriosAsignadosProyectoFinder.obtener(artefacto.getProyectoGrado());
        logger.debug(ArtefactoKey.LOG_VERIFICACION_ASIGNAR,
                proyectoExiste, !asesor.esVacio() && !asesor.estaEliminado(),
                vinculosMasRecientes.size(), versiones.size(), repositoriosAsignados.size());

        asignarArtefactoValidator.validar(
                registro, proyectoExiste, asesor, vinculosMasRecientes, versiones, repositoriosAsignados);

        artefactoOutputPort.registrar(ArtefactoMapper.toEntity(artefacto));
        asignarEstudiantesArtefactoUseCase.ejecutar(registro.getEstudiantes());

        logger.info(ArtefactoKey.LOG_ASIGNADO, artefacto.getId(), artefacto.getProyectoGrado());
        return artefacto.getId();
    }
}
