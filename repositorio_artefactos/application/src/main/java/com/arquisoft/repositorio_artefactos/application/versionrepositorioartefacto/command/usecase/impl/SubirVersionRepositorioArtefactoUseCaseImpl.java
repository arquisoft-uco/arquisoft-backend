package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.RepositorioArtefactoPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.AnalisisCargaFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.CargaPendienteFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.TamanoMaximoArchivoFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.UltimoNumeroVersionDocumentoFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.VersionRepositorioArtefactoSubidaResult;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.mapper.VersionRepositorioArtefactoSubidaResultMapper;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper.VersionRepositorioArtefactoMapper;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.SubirVersionRepositorioArtefactoUseCase;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.SubirVersionRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SubidaVersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.VersionRepositorioArtefactoDomain;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.event.VersionRepositorioArtefactoSubidaEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SubirVersionRepositorioArtefactoUseCaseImpl implements SubirVersionRepositorioArtefactoUseCase {

    private final AdministradorPorIdFinder administradorPorIdFinder;
    private final RepositorioArtefactoPorIdFinder repositorioArtefactoPorIdFinder;
    private final CargaPendienteFinder cargaPendienteFinder;
    private final AnalisisCargaFinder analisisCargaFinder;
    private final UltimoNumeroVersionDocumentoFinder ultimoNumeroVersionDocumentoFinder;
    private final TamanoMaximoArchivoFinder tamanoMaximoArchivoFinder;
    private final SubirVersionRepositorioArtefactoValidator subirVersionValidator;
    private final AlmacenamientoVersionOutputPort almacenamientoVersionOutputPort;
    private final VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public VersionRepositorioArtefactoSubidaResult ejecutar(SubidaVersionRepositorioArtefactoDomain subida) {
        logger.info(VersionRepositorioArtefactoKey.LOG_SUBIENDO,
                subida.getRepositorio(), subida.getCarga(), subida.getSolicitante());

        var administrador = administradorPorIdFinder.obtener(subida.getSolicitante());
        var repositorio = repositorioArtefactoPorIdFinder.obtener(subida.getRepositorio());
        var carga = cargaPendienteFinder.obtener(subida.getCarga());
        var analisis = analisisCargaFinder.obtener(subida.getCarga());
        var ultimoNumero = ultimoNumeroVersionDocumentoFinder.obtener(subida);
        var tamanoMaximo = tamanoMaximoArchivoFinder.obtener();
        logger.debug(VersionRepositorioArtefactoKey.LOG_VERIFICACION_SUBIR,
                !administrador.esVacio(), !repositorio.esVacio(), !carga.esVacio(),
                analisis.getTipoDetectado(), subida.isNuevoDocumento(), ultimoNumero);

        subirVersionValidator.validar(subida, administrador, repositorio, carga, analisis, tamanoMaximo, ultimoNumero);

        var version = VersionRepositorioArtefactoDomain.crear(subida, ultimoNumero, carga, analisis);

        almacenamientoVersionOutputPort.copiarCargaAUri(carga.getCarga(), carga.getEtag(), version.getUriArchivo());
        versionRepositorioArtefactoOutputPort.registrar(VersionRepositorioArtefactoMapper.toEntity(version));
        eventPublisher.publish(new VersionRepositorioArtefactoSubidaEvent(version, repositorio.getNombreRepositorio()));
        almacenamientoVersionOutputPort.eliminarCarga(carga.getCarga());

        logger.info(VersionRepositorioArtefactoKey.LOG_SUBIDA,
                version.getId(), version.getNumeroVersion(), version.getRepositorioArtefacto());
        return VersionRepositorioArtefactoSubidaResultMapper.toResult(version);
    }
}
