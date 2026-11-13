package com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ContextoRegistroEvaluacionAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.ItemsEvaluacionCualitativaAsesorRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.finder.model.ItemsPorEvaluacionAsesor;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.secondaryport.EvaluacionCualitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.usecase.OmitirEvaluacionesCualitativasAsesorUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncualitativaasesor.command.validator.OmitirEvaluacionesCualitativasAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.OmisionEvaluacionesCualitativasAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.EstadoOmisionEvaluacionesCualitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionesCualitativasAsesorRegistradas;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCualitativaAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OmitirEvaluacionesCualitativasAsesorUseCaseImpl
        implements OmitirEvaluacionesCualitativasAsesorUseCase {

    private final ContextoRegistroEvaluacionAsesorFinder contextoRegistroEvaluacionAsesorFinder;
    private final ItemsEvaluacionCualitativaAsesorRegistradosFinder itemsEvaluacionCualitativaAsesorRegistradosFinder;
    private final OmitirEvaluacionesCualitativasAsesorValidator validator;
    private final EvaluacionCualitativaAsesorOutputPort evaluacionCualitativaAsesorOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(OmisionEvaluacionesCualitativasAsesorDomain omision) {
        var evaluacionAsesor = omision.getEvaluacionAsesor();
        var items = omision.getItems();

        logger.info(EvaluacionCualitativaAsesorKey.LOG_OMITIENDO_LOTE, evaluacionAsesor, items.size());

        var contexto = contextoRegistroEvaluacionAsesorFinder.obtener(evaluacionAsesor);
        var itemsRegistrados = itemsEvaluacionCualitativaAsesorRegistradosFinder.obtener(
                new ItemsPorEvaluacionAsesor(evaluacionAsesor, items));

        logger.debug(EvaluacionCualitativaAsesorKey.LOG_VERIFICACION_OMISION,
                evaluacionAsesor, !contexto.esVacio(), contexto.getEstado().getId(), itemsRegistrados.size());

        validator.validar(
                new ExistenciaEvaluacionAsesor(evaluacionAsesor, contexto.getAsesor()),
                new PropiedadEvaluacionAsesor(evaluacionAsesor, contexto.getAsesor(), omision.getAsesor()),
                new EstadoOmisionEvaluacionesCualitativasAsesor(evaluacionAsesor, contexto.getEstado()),
                new ExistenciaEvaluacionesCualitativasAsesorRegistradas(evaluacionAsesor, items, itemsRegistrados));

        evaluacionCualitativaAsesorOutputPort.eliminarPorItems(evaluacionAsesor, items);

        logger.info(EvaluacionCualitativaAsesorKey.LOG_LOTE_OMITIDO, evaluacionAsesor, items.size());
    }
}
