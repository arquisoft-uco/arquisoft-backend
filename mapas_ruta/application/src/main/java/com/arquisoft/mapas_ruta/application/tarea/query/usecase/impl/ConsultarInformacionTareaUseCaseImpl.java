package com.arquisoft.mapas_ruta.application.tarea.query.usecase.impl;

import com.arquisoft.mapas_ruta.application.tarea.query.criteria.TareaCriteria;
import com.arquisoft.mapas_ruta.application.tarea.query.readmodel.TareaReadModel;
import com.arquisoft.mapas_ruta.application.tarea.query.secondaryport.TareaQueryOutputPort;
import com.arquisoft.mapas_ruta.application.tarea.query.usecase.ConsultarInformacionTareaUseCase;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.mapas_ruta.TareaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class ConsultarInformacionTareaUseCaseImpl implements ConsultarInformacionTareaUseCase {

    private final TareaQueryOutputPort tareaQueryOutputPort;
    private final AppLogger logger;

    @Override
    public Optional<TareaReadModel> ejecutar(TareaCriteria criteria) {
        logger.debug(TareaKey.LOG_CONSULTANDO_INFORMACION, criteria.tarea());

        var resultado = tareaQueryOutputPort.consultarPorId(criteria.tarea());

        logger.debug(TareaKey.LOG_CONSULTA_INFORMACION_COMPLETADA, resultado.isPresent());

        return resultado;
    }
}
