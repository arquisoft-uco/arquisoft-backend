package com.arquisoft.artefactos.application.versionartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoBloqueadoPorIdFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudianteArtefactoExisteFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.model.ParticipacionEstudianteArtefacto;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.AnalisisCargaFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.CargaPendienteFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.TamanoMaximoArchivoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.UltimoNumeroVersionArtefactoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.result.VersionArtefactoGeneradaResult;
import com.arquisoft.artefactos.application.versionartefacto.command.result.mapper.VersionArtefactoGeneradaResultMapper;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.VersionArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.mapper.VersionArtefactoMapper;
import com.arquisoft.artefactos.application.versionartefacto.command.usecase.GenerarVersionArtefactoUseCase;
import com.arquisoft.artefactos.application.versionartefacto.command.validator.GenerarVersionArtefactoValidator;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionesRepositorioArtefactoPorIdsFinder;
import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.event.VersionArtefactoGeneradaEvent;
import com.arquisoft.artefactos.domain.versionartefacto.model.CargaAnalizada;
import com.arquisoft.artefactos.domain.versionartefacto.model.ContactoAsesor;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class GenerarVersionArtefactoUseCaseImpl implements GenerarVersionArtefactoUseCase {

    private final ArtefactoBloqueadoPorIdFinder artefactoBloqueadoPorIdFinder;
    private final EstudianteArtefactoExisteFinder estudianteArtefactoExisteFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VersionesRepositorioArtefactoPorIdsFinder versionesRepositorioArtefactoPorIdsFinder;
    private final CargaPendienteFinder cargaPendienteFinder;
    private final AnalisisCargaFinder analisisCargaFinder;
    private final UltimoNumeroVersionArtefactoFinder ultimoNumeroVersionArtefactoFinder;
    private final TamanoMaximoArchivoFinder tamanoMaximoArchivoFinder;
    private final GenerarVersionArtefactoValidator generarVersionValidator;
    private final AlmacenamientoVersionOutputPort almacenamientoVersionOutputPort;
    private final VersionArtefactoOutputPort versionArtefactoOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public VersionArtefactoGeneradaResult ejecutar(GeneracionVersionArtefactoDomain generacion) {
        logger.info(VersionArtefactoKey.LOG_GENERANDO,
                generacion.getArtefacto(), generacion.getCarga(), generacion.getSolicitante());

        var artefacto = artefactoBloqueadoPorIdFinder.obtener(generacion.getArtefacto());
        var estudianteVinculado = estudianteArtefactoExisteFinder.obtener(
                new ParticipacionEstudianteArtefacto(generacion.getSolicitante(), generacion.getArtefacto()));
        var asesor = asesorPorIdFinder.obtener(artefacto.getAsesor());
        var versionesRepositorio = versionesRepositorioArtefactoPorIdsFinder.obtener(
                List.of(artefacto.getVersionRepositorioArtefacto()));
        var carga = cargaPendienteFinder.obtener(generacion.getCarga());
        var tamanoMaximo = tamanoMaximoArchivoFinder.obtener();
        logger.debug(VersionArtefactoKey.LOG_VERIFICACION_GENERAR,
                !artefacto.esVacio(), estudianteVinculado, !asesor.esVacio() && !asesor.estaEliminado(),
                versionesRepositorio.size(), !carga.esVacio());

        generarVersionValidator.validarCarga(
                generacion, artefacto, estudianteVinculado, asesor, versionesRepositorio, carga, tamanoMaximo);

        var analisis = analisisCargaFinder.obtener(generacion.getCarga());
        generarVersionValidator.validarContenido(generacion, new CargaAnalizada(carga, analisis));

        var ultimoNumero = ultimoNumeroVersionArtefactoFinder.obtener(generacion.getArtefacto());
        var version = VersionArtefactoDomain.crear(generacion, ultimoNumero);

        almacenamientoVersionOutputPort.copiarCargaAUri(carga.getCarga(), carga.getEtag(), version.getUriArchivo());
        versionArtefactoOutputPort.registrar(VersionArtefactoMapper.toEntity(version));
        eventPublisher.publish(new VersionArtefactoGeneradaEvent(
                version,
                versionesRepositorio.getFirst().getNombreDocumento(),
                new ContactoAsesor(asesor.getNombre(), asesor.getEmail())));
        almacenamientoVersionOutputPort.eliminarCarga(carga.getCarga());

        logger.info(VersionArtefactoKey.LOG_GENERADA,
                version.getId(), version.getNumeroVersion(), version.getArtefacto());
        return VersionArtefactoGeneradaResultMapper.toResult(version);
    }
}
