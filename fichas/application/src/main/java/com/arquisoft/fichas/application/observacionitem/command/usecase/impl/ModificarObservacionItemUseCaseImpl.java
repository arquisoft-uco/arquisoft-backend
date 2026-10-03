package com.arquisoft.fichas.application.observacionitem.command.usecase.impl;

import com.arquisoft.fichas.application.observacionitem.command.finder.ContextoObservacionItemFinder;
import com.arquisoft.fichas.application.observacionitem.command.finder.OtrasObservacionesIgualesEnRevisionFinder;
import com.arquisoft.fichas.application.observacionitem.command.secondaryport.ObservacionItemOutputPort;
import com.arquisoft.fichas.application.observacionitem.command.usecase.ModificarObservacionItemUseCase;
import com.arquisoft.fichas.application.observacionitem.command.validator.ModificarObservacionItemValidator;
import com.arquisoft.fichas.domain.observacionitem.ModificacionObservacionItemDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.ObservacionItemKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ModificarObservacionItemUseCaseImpl implements ModificarObservacionItemUseCase {

    private final ContextoObservacionItemFinder contextoObservacionItemFinder;
    private final OtrasObservacionesIgualesEnRevisionFinder otrasObservacionesIgualesEnRevisionFinder;
    private final ModificarObservacionItemValidator modificarObservacionItemValidator;
    private final ObservacionItemOutputPort observacionItemOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(ModificacionObservacionItemDomain entrada) {
        logger.info(ObservacionItemKey.LOG_MODIFICANDO, entrada.getObservacionItem(), entrada.getAsesorFicha());

        var contexto = contextoObservacionItemFinder.obtener(entrada.getObservacionItem());
        var observacionExiste = !contexto.esVacio();
        var observacionesIguales = otrasObservacionesIgualesEnRevisionFinder.obtener(entrada);

        logger.debug(ObservacionItemKey.LOG_VERIFICACION_MODIFICAR,
                observacionExiste, contexto.estadoRevision().getId(), contexto.asesorFicha(), observacionesIguales);

        modificarObservacionItemValidator.validar(entrada, contexto, observacionesIguales);

        observacionItemOutputPort.actualizarObservacion(entrada.getObservacionItem(), entrada.getObservacion());

        logger.info(ObservacionItemKey.LOG_MODIFICADA, entrada.getObservacionItem());
    }
}
