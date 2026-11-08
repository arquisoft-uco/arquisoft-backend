package com.arquisoft.mapas_ruta.application.tarea.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.mapas_ruta.application.tarea.command.usecase.CambiarEstadoItemTareaUseCase;
import com.arquisoft.mapas_ruta.application.tarea.command.validator.CambiarEstadoItemTareaValidator;
import com.arquisoft.mapas_ruta.domain.tarea.CambioEstadoItemTareaDomain;
import com.arquisoft.mapas_ruta.domain.tarea.event.TareaFinalizadaEvent;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;
import com.arquisoft.shared.publisher.EventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarEstadoItemTareaUseCaseImpl implements CambiarEstadoItemTareaUseCase {

    private final TareaOutputPort tareaOutputPort;
    private final TareaPorIdFinder tareaPorIdFinder;
    private final CambiarEstadoItemTareaValidator cambiarEstadoItemTareaValidator;
    private final EventPublisher eventPublisher;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioEstadoItemTareaDomain entrada) {
        logger.info(TareaKey.LOG_CAMBIANDO_ESTADO_ITEM, entrada.getTarea(), entrada.getEstadoItem().getId());

        var tarea = tareaPorIdFinder.obtener(entrada.getTarea());

        logger.debug(TareaKey.LOG_VERIFICACION_CAMBIAR_ESTADO_ITEM, !tarea.esVacio());
        cambiarEstadoItemTareaValidator.validar(entrada, tarea);

        tareaOutputPort.actualizarEstadoItem(entrada.getTarea(), entrada.getEstadoItem().getId());

        if (entrada.esFinalizacion()) {
            eventPublisher.publish(new TareaFinalizadaEvent(entrada.getTarea()));
        }

        logger.info(TareaKey.LOG_ESTADO_ITEM_CAMBIADO,
                entrada.getTarea(), tarea.getEstadoItem().getId(), entrada.getEstadoItem().getId());
    }
}
