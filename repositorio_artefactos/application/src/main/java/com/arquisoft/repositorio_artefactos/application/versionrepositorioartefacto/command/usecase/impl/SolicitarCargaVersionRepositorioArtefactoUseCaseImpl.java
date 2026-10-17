package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.administrador.command.finder.AdministradorPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.repositorioartefacto.command.finder.RepositorioArtefactoPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.TamanoMaximoArchivoFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.CargaVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.mapper.CargaVersionRepositorioArtefactoResultMapper;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.mapper.SolicitudCargaMapper;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.SolicitarCargaVersionRepositorioArtefactoUseCase;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.validator.SolicitarCargaVersionRepositorioArtefactoValidator;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.SolicitudCargaVersionRepositorioArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SolicitarCargaVersionRepositorioArtefactoUseCaseImpl
        implements SolicitarCargaVersionRepositorioArtefactoUseCase {

    private final AdministradorPorIdFinder administradorPorIdFinder;
    private final RepositorioArtefactoPorIdFinder repositorioArtefactoPorIdFinder;
    private final TamanoMaximoArchivoFinder tamanoMaximoArchivoFinder;
    private final SolicitarCargaVersionRepositorioArtefactoValidator solicitarCargaValidator;
    private final AlmacenamientoVersionOutputPort almacenamientoVersionOutputPort;
    private final AppLogger logger;

    @Override
    public CargaVersionRepositorioArtefactoResult ejecutar(SolicitudCargaVersionRepositorioArtefactoDomain solicitud) {
        logger.info(VersionRepositorioArtefactoKey.LOG_SOLICITANDO_CARGA,
                solicitud.getRepositorio(), solicitud.getSolicitante());

        var administrador = administradorPorIdFinder.obtener(solicitud.getSolicitante());
        var repositorio = repositorioArtefactoPorIdFinder.obtener(solicitud.getRepositorio());
        var tamanoMaximo = tamanoMaximoArchivoFinder.obtener();
        logger.debug(VersionRepositorioArtefactoKey.LOG_VERIFICACION_SOLICITAR_CARGA,
                !administrador.esVacio(), !repositorio.esVacio());

        solicitarCargaValidator.validar(solicitud, administrador, repositorio, tamanoMaximo);

        var formulario = almacenamientoVersionOutputPort.generarFormularioCarga(
                SolicitudCargaMapper.toEntity(solicitud));

        logger.info(VersionRepositorioArtefactoKey.LOG_CARGA_SOLICITADA, solicitud.getCarga(), formulario.expiraEn());
        return CargaVersionRepositorioArtefactoResultMapper.toResult(solicitud, formulario);
    }
}
