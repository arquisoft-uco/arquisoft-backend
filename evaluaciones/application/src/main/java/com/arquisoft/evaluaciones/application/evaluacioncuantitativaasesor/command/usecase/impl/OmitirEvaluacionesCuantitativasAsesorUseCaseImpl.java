package com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.ContextoRegistroEvaluacionAsesorFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ItemsEvaluacionCuantitativaAsesorRegistradosFinder;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.model.ItemsPorEvaluacionCuantitativaAsesor;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.secondaryport.EvaluacionCuantitativaAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.usecase.OmitirEvaluacionesCuantitativasAsesorUseCase;
import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.validator.OmitirEvaluacionesCuantitativasAsesorValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.ExistenciaEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncualitativaasesor.model.PropiedadEvaluacionAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.OmisionEvaluacionesCuantitativasAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.EstadoOmisionEvaluacionesCuantitativasAsesor;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativaasesor.model.ExistenciaEvaluacionesCuantitativasAsesorRegistradas;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionCuantitativaAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OmitirEvaluacionesCuantitativasAsesorUseCaseImpl
        implements OmitirEvaluacionesCuantitativasAsesorUseCase {

    private final ContextoRegistroEvaluacionAsesorFinder contextoRegistroEvaluacionAsesorFinder;
    private final ItemsEvaluacionCuantitativaAsesorRegistradosFinder itemsEvaluacionCuantitativaAsesorRegistradosFinder;
    private final OmitirEvaluacionesCuantitativasAsesorValidator validator;
    private final EvaluacionCuantitativaAsesorOutputPort evaluacionCuantitativaAsesorOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(OmisionEvaluacionesCuantitativasAsesorDomain omision) {
        var evaluacionAsesor = omision.getEvaluacionAsesor();
        var items = omision.getItems();

        logger.info(EvaluacionCuantitativaAsesorKey.LOG_OMITIENDO_LOTE, evaluacionAsesor, items.size());

        var contexto = contextoRegistroEvaluacionAsesorFinder.obtener(evaluacionAsesor);
        var itemsRegistrados = itemsEvaluacionCuantitativaAsesorRegistradosFinder.obtener(
                new ItemsPorEvaluacionCuantitativaAsesor(evaluacionAsesor, items));

        logger.debug(EvaluacionCuantitativaAsesorKey.LOG_VERIFICACION_OMISION,
                evaluacionAsesor, !contexto.esVacio(), contexto.getEstado().getId(), itemsRegistrados.size());

        validator.validar(
                new ExistenciaEvaluacionAsesor(evaluacionAsesor, contexto.getAsesor()),
                new PropiedadEvaluacionAsesor(evaluacionAsesor, contexto.getAsesor(), omision.getAsesor()),
                new EstadoOmisionEvaluacionesCuantitativasAsesor(evaluacionAsesor, contexto.getEstado()),
                new ExistenciaEvaluacionesCuantitativasAsesorRegistradas(evaluacionAsesor, items, itemsRegistrados));

        evaluacionCuantitativaAsesorOutputPort.eliminarPorItems(evaluacionAsesor, items);

        logger.info(EvaluacionCuantitativaAsesorKey.LOG_LOTE_OMITIDO, evaluacionAsesor, items.size());
    }
}
