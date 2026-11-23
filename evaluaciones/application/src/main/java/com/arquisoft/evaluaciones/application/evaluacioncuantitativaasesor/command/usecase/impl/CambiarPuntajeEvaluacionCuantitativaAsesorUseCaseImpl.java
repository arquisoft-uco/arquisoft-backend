package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.EvaluacionAsesorCompletaFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.usecase.CalcularResumenEvaluacionAsesorUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ContextoEvaluacionCuantitativaAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ItemDeEvaluacionCuantitativaAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.itemcuantitativoasesor.command.finder.ItemCuantitativoAsesorPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.CambiarPuntajeEvaluacionCuantitativaAsesorUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.CambiarPuntajeEvaluacionCuantitativaAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.CompletitudEvaluacionAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.CambioPuntajeEvaluacionCuantitativaAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CambiarPuntajeEvaluacionCuantitativaAsesorUseCaseImpl
        implements CambiarPuntajeEvaluacionCuantitativaAsesorUseCase {

    private final EvaluacionCuantitativaAsesorOutputPort evaluacionCuantitativaAsesorOutputPort;
    private final ContextoEvaluacionCuantitativaAsesorFinder contextoEvaluacionCuantitativaAsesorFinder;
    private final ItemDeEvaluacionCuantitativaAsesorFinder itemDeEvaluacionCuantitativaAsesorFinder;
    private final ItemCuantitativoAsesorPorIdFinder itemCuantitativoAsesorPorIdFinder;
    private final CambiarPuntajeEvaluacionCuantitativaAsesorValidator validator;
    private final EvaluacionAsesorCompletaFinder evaluacionAsesorCompletaFinder;
    private final CalcularResumenEvaluacionAsesorUseCase calcularResumenEvaluacionAsesorUseCase;
    private final AppLogger logger;

    @Override
    public void ejecutar(CambioPuntajeEvaluacionCuantitativaAsesorDomain cambio) {
        logger.info(
                EvaluacionCuantitativaAsesorKey.LOG_CAMBIANDO_PUNTAJE,
                cambio.getEvaluacionCuantitativaAsesor(),
                cambio.getNuevoPuntaje());

        var contexto = contextoEvaluacionCuantitativaAsesorFinder.obtener(cambio.getEvaluacionCuantitativaAsesor());
        var itemId = itemDeEvaluacionCuantitativaAsesorFinder.obtener(cambio.getEvaluacionCuantitativaAsesor());
        var item = itemCuantitativoAsesorPorIdFinder.obtener(itemId);

        logger.debug(
                EvaluacionCuantitativaAsesorKey.LOG_VERIFICACION_CAMBIAR_PUNTAJE,
                !contexto.esVacio() && !item.esVacio(),
                contexto.getEstado().getId(),
                item.getValor());
        validator.validar(cambio, contexto, item);

        evaluacionCuantitativaAsesorOutputPort.actualizarPuntaje(
                cambio.getEvaluacionCuantitativaAsesor(), cambio.getNuevoPuntaje());

        var completa = evaluacionAsesorCompletaFinder.obtener(contexto.getEvaluacionAsesor());
        calcularResumenEvaluacionAsesorUseCase.ejecutar(
                CompletitudEvaluacionAsesorDomain.crear(contexto.getEvaluacionAsesor(), completa));

        logger.info(
                EvaluacionCuantitativaAsesorKey.LOG_PUNTAJE_CAMBIADO, cambio.getEvaluacionCuantitativaAsesor());
    }
}
