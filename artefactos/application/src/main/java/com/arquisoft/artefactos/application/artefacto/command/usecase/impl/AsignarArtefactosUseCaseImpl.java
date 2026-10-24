package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.RepositoriosAsignadosProyectoFinder;
import com.arquisoft.artefactos.application.artefacto.command.result.ArtefactosAsignadosResult;
import com.arquisoft.artefactos.application.artefacto.command.result.mapper.ArtefactosAsignadosResultMapper;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.mapper.ArtefactoMapper;
import com.arquisoft.artefactos.application.artefacto.command.usecase.AsignarArtefactosUseCase;
import com.arquisoft.artefactos.application.artefacto.command.validator.AsignarArtefactosValidator;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.AsesorProyectoGradoPorVinculoFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.model.VinculoAsesorProyectoGrado;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionesRepositorioArtefactoPorIdsFinder;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactosDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsignarArtefactosUseCaseImpl implements AsignarArtefactosUseCase {

    private final ProyectoGradoExisteFinder proyectoGradoExisteFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final AsesorProyectoGradoPorVinculoFinder asesorProyectoGradoPorVinculoFinder;
    private final VersionesRepositorioArtefactoPorIdsFinder versionesRepositorioArtefactoPorIdsFinder;
    private final RepositoriosAsignadosProyectoFinder repositoriosAsignadosProyectoFinder;
    private final AsignarArtefactosValidator asignarArtefactosValidator;
    private final ArtefactoOutputPort artefactoOutputPort;
    private final AppLogger logger;

    @Override
    public ArtefactosAsignadosResult ejecutar(AsignacionArtefactosDomain asignacion) {
        logger.info(ArtefactoKey.LOG_ASIGNANDO, asignacion.getProyectoGrado(), asignacion.getAsesor());

        var proyectoExiste = proyectoGradoExisteFinder.obtener(asignacion.getProyectoGrado());
        var asesor = asesorPorIdFinder.obtener(asignacion.getAsesor());
        var vinculo = asesorProyectoGradoPorVinculoFinder.obtener(
                new VinculoAsesorProyectoGrado(asignacion.getAsesor(), asignacion.getProyectoGrado()));
        var versiones = versionesRepositorioArtefactoPorIdsFinder.obtener(
                asignacion.getVersionesRepositorioArtefacto());
        var repositoriosAsignados = repositoriosAsignadosProyectoFinder.obtener(asignacion.getProyectoGrado());
        logger.debug(ArtefactoKey.LOG_VERIFICACION_ASIGNAR,
                proyectoExiste, !asesor.esVacio() && !asesor.estaEliminado(),
                !vinculo.esVacio() && !vinculo.estaFinalizado(), versiones.size(), repositoriosAsignados.size());

        asignarArtefactosValidator.validar(
                asignacion, proyectoExiste, asesor, vinculo, versiones, repositoriosAsignados);

        artefactoOutputPort.registrarTodos(asignacion.getArtefactos().stream()
                .map(ArtefactoMapper::toEntity)
                .toList());

        logger.info(ArtefactoKey.LOG_ASIGNADOS, asignacion.getProyectoGrado(), asignacion.getArtefactos().size());
        return ArtefactosAsignadosResultMapper.toResult(asignacion);
    }
}
