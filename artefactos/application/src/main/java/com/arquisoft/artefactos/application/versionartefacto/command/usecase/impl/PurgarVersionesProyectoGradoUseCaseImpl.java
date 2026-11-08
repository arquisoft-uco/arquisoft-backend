package com.arquisoft.artefactos.application.versionartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.proyectogrado.command.finder.ProyectoGradoPorIdFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.finder.UrisArchivoVersionesProyectoGradoFinder;
import com.arquisoft.artefactos.application.versionartefacto.command.result.PurgaVersionesProyectoGradoResult;
import com.arquisoft.artefactos.application.versionartefacto.command.result.mapper.PurgaVersionesProyectoGradoResultMapper;
import com.arquisoft.artefactos.application.versionartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.artefactos.application.versionartefacto.command.usecase.PurgarVersionesProyectoGradoUseCase;
import com.arquisoft.artefactos.application.versionartefacto.command.validator.PurgarVersionesProyectoGradoValidator;
import com.arquisoft.artefactos.domain.proyectogrado.model.EstadoProyectoGrado;
import com.arquisoft.artefactos.domain.proyectogrado.model.ExistenciaProyectoGrado;
import com.arquisoft.artefactos.domain.versionartefacto.PurgaVersionesProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.VersionArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PurgarVersionesProyectoGradoUseCaseImpl implements PurgarVersionesProyectoGradoUseCase {

    private final AlmacenamientoVersionOutputPort almacenamientoVersionOutputPort;
    private final ProyectoGradoPorIdFinder proyectoGradoPorIdFinder;
    private final UrisArchivoVersionesProyectoGradoFinder urisArchivoVersionesProyectoGradoFinder;
    private final PurgarVersionesProyectoGradoValidator purgarVersionesProyectoGradoValidator;
    private final AppLogger logger;

    @Override
    public PurgaVersionesProyectoGradoResult ejecutar(PurgaVersionesProyectoGradoDomain purga) {
        var proyecto = proyectoGradoPorIdFinder.obtener(purga.getProyectoGrado());
        var uris = urisArchivoVersionesProyectoGradoFinder.obtener(purga.getProyectoGrado());

        logger.debug(VersionArtefactoKey.LOG_VERIFICACION_PURGAR,
                !proyecto.esVacio(), proyecto.estaFinalizado(), uris.size());

        purgarVersionesProyectoGradoValidator.validar(
                new ExistenciaProyectoGrado(purga.getProyectoGrado(), !proyecto.esVacio()),
                new EstadoProyectoGrado(
                        purga.getProyectoGrado(), proyecto.getNombre(),
                        proyecto.estaCongelado(), proyecto.estaFinalizado()));

        for (var uri : uris) {
            almacenamientoVersionOutputPort.eliminarArchivo(uri);
        }

        return PurgaVersionesProyectoGradoResultMapper.toResult(purga, uris.size());
    }
}
