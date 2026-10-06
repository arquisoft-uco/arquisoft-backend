package com.arquisoft.fichas.application.observacionitem.command.usecase.impl;

import com.arquisoft.fichas.application.observacionitem.command.finder.ContextoObservacionItemFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.usecase.RemoverObservacionItemUseCase;
import com.arquisoft.fichas.application.observacionitem.command.validator.RemoverObservacionItemValidator;
import com.arquisoft.fichas.domain.observacionitem.RemocionObservacionItemDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverObservacionItemUseCaseImpl implements RemoverObservacionItemUseCase {

    private final ContextoObservacionItemFinder contextoObservacionItemFinder;
    private final RemoverObservacionItemValidator removerObservacionItemValidator;
    private final ObservacionItemOutputPort observacionItemOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionObservacionItemDomain entrada) {
        logger.info(ObservacionItemKey.LOG_REMOVIENDO, entrada.getObservacionItem(), entrada.getAsesorFicha());

        var contexto = contextoObservacionItemFinder.obtener(entrada.getObservacionItem());
        var observacionExiste = !contexto.esVacio();

        logger.debug(ObservacionItemKey.LOG_VERIFICACION_REMOVER,
                observacionExiste, contexto.estadoRevision().getId(), contexto.asesorFicha());

        removerObservacionItemValidator.validar(entrada, contexto);

        observacionItemOutputPort.removerObservacion(entrada.getObservacionItem());

        logger.info(ObservacionItemKey.LOG_REMOVIDA, entrada.getObservacionItem());
    }
}
