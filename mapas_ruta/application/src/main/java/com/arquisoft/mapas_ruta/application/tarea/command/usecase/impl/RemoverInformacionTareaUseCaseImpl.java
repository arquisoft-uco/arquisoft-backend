package com.arquisoft.mapas_ruta.application.tarea.command.usecase.impl;

import com.arquisoft.mapas_ruta.application.tarea.command.finder.TareaPorIdFinder;
import com.arquisoft.mapas_ruta.application.tarea.command.secondaryport.TareaOutputPort;
import com.arquisoft.mapas_ruta.application.tarea.command.usecase.RemoverInformacionTareaUseCase;
import com.arquisoft.mapas_ruta.application.tarea.command.validator.RemoverInformacionTareaValidator;
import com.arquisoft.mapas_ruta.domain.tarea.RemocionTareaDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverInformacionTareaUseCaseImpl implements RemoverInformacionTareaUseCase {

    private final TareaOutputPort tareaOutputPort;
    private final TareaPorIdFinder tareaPorIdFinder;
    private final RemoverInformacionTareaValidator removerInformacionTareaValidator;
    private final AppLogger logger;

    @Override
    public void ejecutar(RemocionTareaDomain entrada) {
        logger.info(TareaKey.LOG_REMOVIENDO_INFORMACION, entrada.getTarea());

        var tarea = tareaPorIdFinder.obtener(entrada.getTarea());

        logger.debug(TareaKey.LOG_VERIFICACION_REMOVER_INFORMACION,
                !tarea.esVacio(), tarea.getEstadoItem().getId());
        removerInformacionTareaValidator.validar(entrada, tarea);

        tareaOutputPort.eliminar(entrada.getTarea());

        logger.info(TareaKey.LOG_INFORMACION_REMOVIDA, entrada.getTarea(), tarea.getMapaRuta());
    }
}
