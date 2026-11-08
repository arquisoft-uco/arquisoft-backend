package com.arquisoft.artefactos.application.versionartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.EstudianteArtefactoExisteFinder;
import com.arquisoft.artefactos.application.estudianteartefacto.command.finder.model.ParticipacionEstudianteArtefacto;
import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorArtefactoFinder;
import com.arquisoft.artefactos.application.revisionasesor.command.finder.RevisionVigenteUltimaVersionFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.CuotaProyectoGradoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.TamanoMaximoArchivoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.TamanoVigenteProyectoGradoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.UltimoNumeroVersionArtefactoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.result.CargaVersionArtefactoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.result.mapper.CargaVersionArtefactoResultMapper;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.mapper.SolicitudCargaMapper;
import com.arquisoft.artefactos.application.versionartefacto.command.usecase.SolicitarCargaVersionArtefactoUseCase;
import com.arquisoft.artefactos.application.versionartefacto.command.validator.SolicitarCargaVersionArtefactoValidator;
import com.arquisoft.artefactos.domain.versionartefacto.SolicitudCargaVersionArtefactoDomain;
import com.arquisoft.artefactos.domain.versionartefacto.model.AlmacenamientoProyectoGrado;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SolicitarCargaVersionArtefactoUseCaseImpl implements SolicitarCargaVersionArtefactoUseCase {

    private final ArtefactoPorIdFinder artefactoPorIdFinder;
    private final EstudianteArtefactoExisteFinder estudianteArtefactoExisteFinder;
    private final TamanoMaximoArchivoFinder tamanoMaximoArchivoFinder;
    private final ProyectoGradoPorArtefactoFinder proyectoGradoPorArtefactoFinder;
    private final CuotaProyectoGradoFinder cuotaProyectoGradoFinder;
    private final TamanoVigenteProyectoGradoFinder tamanoVigenteProyectoGradoFinder;
    private final UltimoNumeroVersionArtefactoFinder ultimoNumeroVersionArtefactoFinder;
    private final RevisionVigenteUltimaVersionFinder revisionVigenteUltimaVersionFinder;
    private final SolicitarCargaVersionArtefactoValidator solicitarCargaValidator;
    private final AlmacenamientoVersionOutputPort almacenamientoVersionOutputPort;
    private final AppLogger logger;

    @Override
    public CargaVersionArtefactoResult ejecutar(SolicitudCargaVersionArtefactoDomain solicitud) {
        logger.info(VersionArtefactoKey.LOG_SOLICITANDO_CARGA, solicitud.getArtefacto(), solicitud.getSolicitante());

        var artefacto = artefactoPorIdFinder.obtener(solicitud.getArtefacto());
        var estudianteVinculado = estudianteArtefactoExisteFinder.obtener(
                new ParticipacionEstudianteArtefacto(solicitud.getSolicitante(), solicitud.getArtefacto()));
        var tamanoMaximo = tamanoMaximoArchivoFinder.obtener();
        var ultimoNumero = ultimoNumeroVersionArtefactoFinder.obtener(solicitud.getArtefacto());
        var revisionVigente = revisionVigenteUltimaVersionFinder.obtener(solicitud.getArtefacto());
        var proyectoGrado = proyectoGradoPorArtefactoFinder.obtener(solicitud.getArtefacto());
        var cuota = cuotaProyectoGradoFinder.obtener();
        var uso = tamanoVigenteProyectoGradoFinder.obtener(solicitud.getArtefacto());
        logger.debug(VersionArtefactoKey.LOG_VERIFICACION_SOLICITAR_CARGA, !artefacto.esVacio(), estudianteVinculado,
                ultimoNumero, revisionVigente.getEstado().getId(), !proyectoGrado.esVacio(), uso);

        solicitarCargaValidator.validar(
                solicitud, artefacto, estudianteVinculado, tamanoMaximo, ultimoNumero, revisionVigente,
                new AlmacenamientoProyectoGrado(proyectoGrado, cuota, uso));

        var formulario = almacenamientoVersionOutputPort.generarFormularioCarga(
                SolicitudCargaMapper.toEntity(solicitud));

        logger.info(VersionArtefactoKey.LOG_CARGA_SOLICITADA, solicitud.getCarga(), formulario.expiraEn());
        return CargaVersionArtefactoResultMapper.toResult(solicitud, formulario);
    }
}
