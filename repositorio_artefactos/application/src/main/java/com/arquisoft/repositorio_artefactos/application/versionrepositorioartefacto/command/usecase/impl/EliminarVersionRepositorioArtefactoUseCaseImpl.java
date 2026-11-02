package com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.impl;

import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.finder.ArchivoVersionPorIdFinder;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.EliminacionVersionRepositorioArtefactoResult;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.result.mapper.EliminacionVersionRepositorioArtefactoResultMapper;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.AlmacenamientoVersionOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.repositorio_artefactos.application.versionrepositorioartefacto.command.usecase.EliminarVersionRepositorioArtefactoUseCase;
import com.arquisoft.repositorio_artefactos.domain.versionrepositorioartefacto.EliminacionVersionRepositorioArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.repositorio_artefactos.VersionRepositorioArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarVersionRepositorioArtefactoUseCaseImpl implements EliminarVersionRepositorioArtefactoUseCase {

    private final ArchivoVersionPorIdFinder archivoVersionPorIdFinder;
    private final AlmacenamientoVersionOutputPort almacenamientoVersionOutputPort;
    private final VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;
    private final AppLogger logger;

    @Override
    public EliminacionVersionRepositorioArtefactoResult ejecutar(
            EliminacionVersionRepositorioArtefactoDomain eliminacion) {
        var archivo = archivoVersionPorIdFinder.obtener(eliminacion.getVersion());
        logger.debug(VersionRepositorioArtefactoKey.LOG_VERIFICACION_ELIMINAR,
                eliminacion.getVersion(), !archivo.esVacio());

        if (archivo.esVacio()) {
            return EliminacionVersionRepositorioArtefactoResultMapper.toResultInexistente(eliminacion);
        }

        almacenamientoVersionOutputPort.eliminarArchivo(archivo.getUriArchivo());
        versionRepositorioArtefactoOutputPort.eliminar(eliminacion.getVersion());

        return EliminacionVersionRepositorioArtefactoResultMapper.toResultEliminada(eliminacion);
    }
}
