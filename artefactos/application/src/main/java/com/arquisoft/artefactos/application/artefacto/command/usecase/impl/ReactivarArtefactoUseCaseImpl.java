package com.arquisoft.artefactos.application.artefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoBloqueadoPorIdFinder;
import com.arquisoft.artefactos.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.artefactos.application.artefacto.command.usecase.ReactivarArtefactoUseCase;
import com.arquisoft.artefactos.application.artefacto.command.validator.ReactivarArtefactoValidator;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.asesorproyectogrado.command.finder.VinculosMasRecientesProyectoGradoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudiantesVigentesVinculadosArtefactoFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.AsignarEstudiantesArtefactoUseCase;
import com.arquisoft.artefactos.application.estudianteartefacto.command.usecase.DepurarEstudiantesArtefactoUseCase;
import com.arquisoft.artefactos.application.estudianteproyectogrado.command.finder.EstudiantesVigentesProyectoGradoFinder;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoBloqueadoPorArtefactoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.CuotaProyectoGradoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.TamanoVigenteArtefactoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.TamanoVigenteProyectoGradoFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.artefacto.ReactivacionArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.event.ArtefactoReactivadoEvent;
import com.arquisoft.artefactos.domain.estudianteartefacto.AgregacionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.DepuracionEstudiantesArtefactoDomain;
import com.arquisoft.artefactos.domain.estudianteartefacto.EstudianteArtefactoDomain;
import com.arquisoft.artefactos.domain.artefacto.model.PesoArtefactoReactivable;
import com.arquisoft.artefactos.domain.estudianteartefacto.model.CupoEstudiantesArtefacto;
import com.arquisoft.artefactos.domain.versionartefacto.model.AlmacenamientoProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.ArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import com.arquisoft.shared.util.UtilColeccion;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactivarArtefactoUseCaseImpl implements ReactivarArtefactoUseCase {

    private final ArtefactoBloqueadoPorIdFinder artefactoBloqueadoPorIdFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VinculosMasRecientesProyectoGradoFinder vinculosMasRecientesProyectoGradoFinder;
    private final EstudiantesVigentesProyectoGradoFinder estudiantesVigentesProyectoGradoFinder;
    private final EstudiantesVigentesVinculadosArtefactoFinder estudiantesVigentesVinculadosArtefactoFinder;
    private final VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    private final TamanoVigenteArtefactoFinder tamanoVigenteArtefactoFinder;
    private final ProyectoGradoBloqueadoPorArtefactoFinder proyectoGradoBloqueadoPorArtefactoFinder;
    private final CuotaProyectoGradoFinder cuotaProyectoGradoFinder;
    private final TamanoVigenteProyectoGradoFinder tamanoVigenteProyectoGradoFinder;
    private final ReactivarArtefactoValidator reactivarArtefactoValidator;
    private final ArtefactoOutputPort artefactoOutputPort;
    private final DepurarEstudiantesArtefactoUseCase depurarEstudiantesArtefactoUseCase;
    private final AsignarEstudiantesArtefactoUseCase asignarEstudiantesArtefactoUseCase;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(ReactivacionArtefactoDomain reactivacion) {
        var artefacto = artefactoBloqueadoPorIdFinder.obtener(reactivacion.getArtefacto());
        var asesor = asesorPorIdFinder.obtener(reactivacion.getAsesor());
        var vinculosMasRecientes = vinculosMasRecientesProyectoGradoFinder.obtener(reactivacion.getProyectoGrado());
        var vigentesProyecto = estudiantesVigentesProyectoGradoFinder.obtener(reactivacion.getProyectoGrado());
        var vinculadosVigentes = estudiantesVigentesVinculadosArtefactoFinder.obtener(reactivacion.getArtefacto());
        var versionRepositorio = versionRepositorioArtefactoPorIdFinder.obtener(
                artefacto.getVersionRepositorioArtefacto());
        var pesoVigente = tamanoVigenteArtefactoFinder.obtener(reactivacion.getArtefacto());
        var proyectoGrado = proyectoGradoBloqueadoPorArtefactoFinder.obtener(reactivacion.getArtefacto());
        var cuotaBytes = cuotaProyectoGradoFinder.obtener();
        var usoBytes = tamanoVigenteProyectoGradoFinder.obtener(reactivacion.getArtefacto());
        logger.debug(ArtefactoKey.LOG_VERIFICACION_REACTIVAR,
                !asesor.esVacio() && !asesor.estaEliminado(), vinculosMasRecientes.size(),
                vigentesProyecto.size(), vinculadosVigentes.size(), !proyectoGrado.esVacio(), usoBytes, pesoVigente);

        var cupo = new CupoEstudiantesArtefacto(reactivacion.getEstudiantes(), vinculadosVigentes, vigentesProyecto);
        reactivarArtefactoValidator.validar(reactivacion, asesor, vinculosMasRecientes, cupo,
                new PesoArtefactoReactivable(artefacto, versionRepositorio, pesoVigente),
                new AlmacenamientoProyectoGrado(proyectoGrado, cuotaBytes, usoBytes));

        artefactoOutputPort.reactivar(reactivacion.getArtefacto(), reactivacion.getAsesor());
        eventPublisher.publish(new ArtefactoReactivadoEvent(
                reactivacion.getArtefacto(), artefacto.getProyectoGrado()));
        depurarEstudiantesArtefactoUseCase.ejecutar(DepuracionEstudiantesArtefactoDomain.crear(
                reactivacion.getArtefacto(), reactivacion.getEstudiantes()));

        var nuevos = cupo.nuevos();
        if (!UtilColeccion.esVaciaONula(nuevos)) {
            var relaciones = EstudianteArtefactoDomain.crear(reactivacion.getArtefacto(), nuevos);
            asignarEstudiantesArtefactoUseCase.ejecutar(
                    AgregacionEstudiantesArtefactoDomain.crear(reactivacion.getAsesor(), relaciones));
        }
    }
}
