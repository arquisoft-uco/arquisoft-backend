package com.arquisoft.entregables.application.artefacto.command.usecase.impl;

import com.arquisoft.entregables.application.artefacto.command.finder.ArtefactoPorIdFinder;
import com.arquisoft.entregables.application.artefacto.command.result.ReactivacionArtefactoResult;
import com.arquisoft.entregables.application.artefacto.command.result.mapper.ReactivacionArtefactoResultMapper;
import com.arquisoft.entregables.application.artefacto.command.secondaryport.ArtefactoOutputPort;
import com.arquisoft.entregables.application.artefacto.command.usecase.ReactivarArtefactoUseCase;
import com.arquisoft.entregables.domain.artefacto.ReactivacionArtefactoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ArtefactoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReactivarArtefactoUseCaseImpl implements ReactivarArtefactoUseCase {

    private final ArtefactoPorIdFinder artefactoPorIdFinder;
    private final ArtefactoOutputPort artefactoOutputPort;
    private final AppLogger logger;

    @Override
    public ReactivacionArtefactoResult ejecutar(ReactivacionArtefactoDomain reactivacion) {
        var artefacto = artefactoPorIdFinder.obtener(reactivacion.getArtefacto());
        logger.debug(ArtefactoKey.LOG_VERIFICACION_OMISION, !artefacto.esVacio(), artefacto.getOmisionOcurridaEn(),
                reactivacion.getOcurridoEn());

        if (artefacto.esVacio()) {
            return ReactivacionArtefactoResultMapper.toResultSinVersionCerrada(reactivacion);
        }
        if (artefacto.tieneOmisionIgualOPosteriorA(reactivacion.getOcurridoEn())) {
            return ReactivacionArtefactoResultMapper.toResultDescartada(reactivacion);
        }

        artefactoOutputPort.limpiarOmision(reactivacion.getArtefacto(), reactivacion.getOcurridoEn());
        return ReactivacionArtefactoResultMapper.toResultReactivada(reactivacion);
    }
}
