package com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase.impl;

import com.arquisoft.artefactos.application.artefacto.command.finder.ArtefactoPorVersionRepositorioExisteFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.UltimaVersionDocumentoDeVersionRepositorioFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.finder.VersionRepositorioArtefactoBloqueadaPorIdFinder;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.secondaryport.VersionRepositorioArtefactoOutputPort;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.usecase.EliminarVersionRepositorioArtefactoUseCase;
import com.arquisoft.artefactos.application.versionrepositorioartefacto.command.validator.EliminarVersionRepositorioArtefactoValidator;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.EliminacionVersionRepositorioArtefactoDomain;
import com.arquisoft.artefactos.domain.versionrepositorioartefacto.event.VersionRepositorioArtefactoEliminacionAutorizadaEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.artefactos.EliminarVersionRepositorioArtefactoKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EliminarVersionRepositorioArtefactoUseCaseImpl implements EliminarVersionRepositorioArtefactoUseCase {

    private final VersionRepositorioArtefactoBloqueadaPorIdFinder versionRepositorioArtefactoBloqueadaPorIdFinder;
    private final UltimaVersionDocumentoDeVersionRepositorioFinder ultimaVersionDocumentoDeVersionRepositorioFinder;
    private final ArtefactoPorVersionRepositorioExisteFinder artefactoPorVersionRepositorioExisteFinder;
    private final EliminarVersionRepositorioArtefactoValidator eliminarVersionRepositorioArtefactoValidator;
    private final VersionRepositorioArtefactoOutputPort versionRepositorioArtefactoOutputPort;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(EliminacionVersionRepositorioArtefactoDomain eliminacion) {
        logger.info(EliminarVersionRepositorioArtefactoKey.LOG_ELIMINANDO, eliminacion.getVersion());

        var version = versionRepositorioArtefactoBloqueadaPorIdFinder.obtener(eliminacion.getVersion());
        var ultimaVersionDocumento = ultimaVersionDocumentoDeVersionRepositorioFinder.obtener(eliminacion.getVersion());
        var enUso = artefactoPorVersionRepositorioExisteFinder.obtener(eliminacion.getVersion());
        logger.debug(EliminarVersionRepositorioArtefactoKey.LOG_VERIFICACION_ELIMINAR,
                !version.esVacio(), ultimaVersionDocumento, enUso);

        eliminarVersionRepositorioArtefactoValidator.validar(
                eliminacion, version, ultimaVersionDocumento, enUso);

        versionRepositorioArtefactoOutputPort.eliminar(eliminacion.getVersion());

        eventPublisher.publish(new VersionRepositorioArtefactoEliminacionAutorizadaEvent(version));

        logger.info(EliminarVersionRepositorioArtefactoKey.LOG_ELIMINADA,
                eliminacion.getVersion(), version.getRepositorioArtefacto());
    }
}
