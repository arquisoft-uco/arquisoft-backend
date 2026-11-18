package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.VersionesAsignadasProyectoFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.mapper.ArtefactoMapper;
import com.arquisoft.artefactos.application.artefacto.command.usecase.RegistrarArtefactoUseCase;
import com.arquisoft.artefactos.application.artefacto.command.validator.RegistrarArtefactoValidator;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.VinculosMasRecientesProyectoGradoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.AsignarEstudiantesArtefactoUseCase;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorIdFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.validator.ProyectoGradoNoCongeladoValidator;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoBloqueadaPorIdFinder;
import com.arquisoft.artefactos.domain.artefacto.AsignacionArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import com.arquisoft.shared.message.key.artefactos.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarArtefactoUseCaseImpl implements RegistrarArtefactoUseCase {

    private final ProyectoGradoBloqueadoPorIdFinder proyectoGradoBloqueadoPorIdFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VinculosMasRecientesProyectoGradoFinder vinculosMasRecientesProyectoGradoFinder;
    private final VersionRepositorioArtefactoBloqueadaPorIdFinder versionRepositorioArtefactoBloqueadaPorIdFinder;
    private final VersionesAsignadasProyectoFinder versionesAsignadasProyectoFinder;
    private final RegistrarArtefactoValidator registrarArtefactoValidator;
    private final ArtefactoOutputPort artefactoOutputPort;
    private final AsignarEstudiantesArtefactoUseCase asignarEstudiantesArtefactoUseCase;
    private final ProyectoGradoNoCongeladoValidator proyectoGradoNoCongeladoValidator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(AsignacionArtefactoDomain registro) {
        var artefacto = registro.getArtefacto();

        var asesor = asesorPorIdFinder.obtener(artefacto.getAsesor());
        var vinculosMasRecientes = vinculosMasRecientesProyectoGradoFinder.obtener(artefacto.getProyectoGrado());
        var versionRepositorio = versionRepositorioArtefactoBloqueadaPorIdFinder.obtener(
                artefacto.getVersionRepositorioArtefacto());
        var versionesAsignadas = versionesAsignadasProyectoFinder.obtener(artefacto.getProyectoGrado());
        var proyecto = proyectoGradoBloqueadoPorIdFinder.obtener(artefacto.getProyectoGrado());
        logger.debug(ArtefactoKey.LOG_VERIFICACION_REGISTRAR,
                !proyecto.esVacio(), !asesor.esVacio() && !asesor.estaEliminado(),
                vinculosMasRecientes.size(), !versionRepositorio.esVacio(), versionesAsignadas.size());

        registrarArtefactoValidator.validar(
                registro, !proyecto.esVacio(), asesor, vinculosMasRecientes, versionRepositorio, versionesAsignadas);

        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_CONGELAMIENTO, !proyecto.esVacio(), proyecto.estaCongelado());
        proyectoGradoNoCongeladoValidator.validar(proyecto);

        artefactoOutputPort.registrar(ArtefactoMapper.toEntity(artefacto));
        asignarEstudiantesArtefactoUseCase.ejecutar(registro.getEstudiantes());

        return artefacto.getId();
    }
}
