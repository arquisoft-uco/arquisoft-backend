package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.RepositoriosAsignadosProyectoFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.mapper.ArtefactoMapper;
import com.arquisoft.artefactos.application.artefacto.command.usecase.RegistrarArtefactoUseCase;
import com.arquisoft.artefactos.application.artefacto.command.validator.RegistrarArtefactoValidator;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.VinculosMasRecientesProyectoGradoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.AsignarEstudiantesArtefactoUseCase;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarArtefactoUseCaseImpl implements RegistrarArtefactoUseCase {

    private final ProyectoGradoExisteFinder proyectoGradoExisteFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VinculosMasRecientesProyectoGradoFinder vinculosMasRecientesProyectoGradoFinder;
    private final VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    private final RepositoriosAsignadosProyectoFinder repositoriosAsignadosProyectoFinder;
    private final RegistrarArtefactoValidator registrarArtefactoValidator;
    private final ArtefactoOutputPort artefactoOutputPort;
    private final AsignarEstudiantesArtefactoUseCase asignarEstudiantesArtefactoUseCase;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AsignacionArtefactoDomain registro) {
        var artefacto = registro.getArtefacto();

        var proyectoExiste = proyectoGradoExisteFinder.obtener(artefacto.getProyectoGrado());
        var asesor = asesorPorIdFinder.obtener(artefacto.getAsesor());
        var vinculosMasRecientes = vinculosMasRecientesProyectoGradoFinder.obtener(artefacto.getProyectoGrado());
        var versionRepositorio = versionRepositorioArtefactoPorIdFinder.obtener(
                artefacto.getVersionRepositorioArtefacto());
        var repositoriosAsignados = repositoriosAsignadosProyectoFinder.obtener(artefacto.getProyectoGrado());
        logger.debug(ArtefactoKey.LOG_VERIFICACION_REGISTRAR,
                proyectoExiste, !asesor.esVacio() && !asesor.estaEliminado(),
                vinculosMasRecientes.size(), !versionRepositorio.esVacio(), repositoriosAsignados.size());

        registrarArtefactoValidator.validar(
                registro, proyectoExiste, asesor, vinculosMasRecientes, versionRepositorio, repositoriosAsignados);

        artefactoOutputPort.registrar(ArtefactoMapper.toEntity(artefacto));
        asignarEstudiantesArtefactoUseCase.ejecutar(registro.getEstudiantes());

        return artefacto.getId();
    }
}
