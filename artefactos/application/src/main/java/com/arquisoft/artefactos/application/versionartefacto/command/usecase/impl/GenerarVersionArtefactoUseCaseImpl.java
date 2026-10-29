package com.arquisoft.artefactos.application.versionartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoBloqueadoPorIdFinder;
import com.arquisoft.artefactos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudianteArtefactoExisteFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.model.ParticipacionEstudianteArtefacto;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionVigenteUltimaVersionFinder;
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
import com.arquisoft.artefactos.application.versionartefacto.command.validator.GenerarVersionArtefactoContenidoValidator;
import com.arquisoft.artefactos.application.versionartefacto.command.validator.GenerarVersionArtefactoValidator;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoPorIdFinder;
import com.arquisoft.artefactos.domain.versionartefacto.GeneracionVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.VersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.event.VersionArtefactoGeneradaEvent;
import com.arquisoft.artefactos.domain.revisionasesor.model.RevisionVersionAnterior;
import com.arquisoft.artefactos.domain.versionartefacto.model.CargaAnalizada;
import com.arquisoft.artefactos.domain.versionartefacto.model.ContactoAsesor;
import com.arquisoft.artefactos.domain.versionartefacto.model.PrecondicionesGeneracionVersion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GenerarVersionArtefactoUseCaseImpl implements GenerarVersionArtefactoUseCase {

    private final ArtefactoBloqueadoPorIdFinder artefactoBloqueadoPorIdFinder;
    private final EstudianteArtefactoExisteFinder estudianteArtefactoExisteFinder;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final VersionRepositorioArtefactoPorIdFinder versionRepositorioArtefactoPorIdFinder;
    private final CargaPendienteFinder cargaPendienteFinder;
    private final AnalisisCargaFinder analisisCargaFinder;
    private final UltimoNumeroVersionArtefactoFinder ultimoNumeroVersionArtefactoFinder;
    private final RevisionVigenteUltimaVersionFinder revisionVigenteUltimaVersionFinder;
    private final TamanoMaximoArchivoFinder tamanoMaximoArchivoFinder;
    private final GenerarVersionArtefactoValidator generarVersionValidator;
    private final GenerarVersionArtefactoContenidoValidator generarVersionContenidoValidator;
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
        var versionRepositorio = versionRepositorioArtefactoPorIdFinder.obtener(
                artefacto.getVersionRepositorioArtefacto());
        var carga = cargaPendienteFinder.obtener(generacion.getCarga());
        var tamanoMaximo = tamanoMaximoArchivoFinder.obtener();
        var ultimoNumero = ultimoNumeroVersionArtefactoFinder.obtener(generacion.getArtefacto());
        var revisionVigente = revisionVigenteUltimaVersionFinder.obtener(generacion.getArtefacto());
        logger.debug(VersionArtefactoKey.LOG_VERIFICACION_GENERAR,
                !artefacto.esVacio(), estudianteVinculado, !asesor.esVacio() && !asesor.estaEliminado(),
                !versionRepositorio.esVacio(), !carga.esVacio(), ultimoNumero, revisionVigente.getEstado().getId());

        generarVersionValidator.validar(generacion, new PrecondicionesGeneracionVersion(
                artefacto, estudianteVinculado, asesor, versionRepositorio, carga, tamanoMaximo,
                new RevisionVersionAnterior(ultimoNumero, revisionVigente)));

        var analisis = analisisCargaFinder.obtener(generacion.getCarga());
        generarVersionContenidoValidator.validar(generacion, new CargaAnalizada(carga, analisis));

        var version = VersionArtefactoDomain.crear(generacion, ultimoNumero);

        almacenamientoVersionOutputPort.copiarCargaAUri(carga.getCarga(), carga.getEtag(), version.getUriArchivo());
        versionArtefactoOutputPort.registrar(VersionArtefactoMapper.toEntity(version));
        eventPublisher.publish(new VersionArtefactoGeneradaEvent(
                version,
                versionRepositorio.getNombreDocumento(),
                new ContactoAsesor(asesor.getNombre(), asesor.getEmail())));
        almacenamientoVersionOutputPort.eliminarCarga(carga.getCarga());

        logger.info(VersionArtefactoKey.LOG_GENERADA,
                version.getId(), version.getNumeroVersion(), version.getArtefacto());
        return VersionArtefactoGeneradaResultMapper.toResult(version);
    }
}
