package com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.EvaluacionCuantitativaJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.secondaryport.EvaluacionCuantitativaJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.usecase.CambiarPuntajeEvaluacionCuantitativaJuradoUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.validator.CambiarPuntajeEvaluacionCuantitativaJuradoValidator;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoEstadoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model.SolicitudEstadoEvaluacionJurado;
import com.arquisoft.evaluaciones.application.itemcuantitativojurado.command.finder.ItemCuantitativoJuradoPorIdFinder;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.CambioPuntajeEvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.itemcuantitativojurado.ItemCuantitativoJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarPuntajeEvaluacionCuantitativaJuradoUseCaseImpl
        implements CambiarPuntajeEvaluacionCuantitativaJuradoUseCase {

    private final EvaluacionCuantitativaJuradoOutputPort evaluacionCuantitativaJuradoOutputPort;
    private final EvaluacionCuantitativaJuradoPorIdFinder evaluacionCuantitativaJuradoPorIdFinder;
    private final EvaluacionJuradoEstadoFinder evaluacionJuradoEstadoFinder;
    private final ItemCuantitativoJuradoPorIdFinder itemCuantitativoJuradoPorIdFinder;
    private final CambiarPuntajeEvaluacionCuantitativaJuradoValidator validator;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioPuntajeEvaluacionCuantitativaJuradoDomain cambio) {
        logger.info(
                EvaluacionCuantitativaJuradoKey.LOG_CAMBIANDO_PUNTAJE,
                cambio.getEvaluacionCuantitativaJurado(),
                cambio.getNuevoPuntaje());

        var evaluacion = evaluacionCuantitativaJuradoPorIdFinder
                .obtener(cambio.getEvaluacionCuantitativaJurado())
                .orElse(EvaluacionCuantitativaJuradoDomain.VACIO);

        var estado = evaluacionJuradoEstadoFinder.obtener(
                new SolicitudEstadoEvaluacionJurado(evaluacion.getEvaluacionJurado(), cambio.getJurado()));

        var item = itemCuantitativoJuradoPorIdFinder.obtener(evaluacion.getItem());
        var valorMaximoItem = item.map(ItemCuantitativoJuradoDomain::getValor).orElse(Integer.MAX_VALUE);

        logger.debug(
                EvaluacionCuantitativaJuradoKey.LOG_VERIFICACION_CAMBIAR_PUNTAJE,
                !evaluacion.esVacio(),
                estado.pertenece(),
                estado.finalizada());
        validator.validar(cambio, evaluacion, estado, valorMaximoItem);

        evaluacionCuantitativaJuradoOutputPort.cambiarPuntaje(
                cambio.getEvaluacionCuantitativaJurado(), cambio.getNuevoPuntaje());

        logger.info(EvaluacionCuantitativaJuradoKey.LOG_PUNTAJE_CAMBIADO, cambio.getEvaluacionCuantitativaJurado());
    }
}
