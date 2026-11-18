package com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativaasesor.command.finder.ContextoEvaluacionCuantitativaAsesorFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.DescripcionObservacionItemAsesorExisteFinder;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.finder.model.CriterioDescripcionObservacionItemAsesor;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.ObservacionItemAsesorOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.secondaryport.mapper.ObservacionItemAsesorMapper;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.usecase.RegistrarObservacionItemAsesorUseCase;
import com.arquisoft.evaluaciones.application.observacionitemasesor.command.validator.RegistrarObservacionItemAsesorValidator;
import com.arquisoft.evaluaciones.domain.observacionitemasesor.RegistroObservacionItemAsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarObservacionItemAsesorUseCaseImpl implements RegistrarObservacionItemAsesorUseCase {

    private final ObservacionItemAsesorOutputPort observacionItemAsesorOutputPort;
    private final ContextoEvaluacionCuantitativaAsesorFinder contextoEvaluacionCuantitativaAsesorFinder;
    private final DescripcionObservacionItemAsesorExisteFinder descripcionObservacionItemAsesorExisteFinder;
    private final RegistrarObservacionItemAsesorValidator validator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(RegistroObservacionItemAsesorDomain registro) {
        var observacion = registro.getObservacion();
        logger.info(ObservacionItemAsesorKey.LOG_REGISTRANDO, observacion.getEvaluacionCuantitativaAsesor());

        var contexto = contextoEvaluacionCuantitativaAsesorFinder
                .obtener(observacion.getEvaluacionCuantitativaAsesor());
        var descripcionYaExiste = descripcionObservacionItemAsesorExisteFinder.obtener(
                new CriterioDescripcionObservacionItemAsesor(
                        observacion.getEvaluacionCuantitativaAsesor(), observacion.getDescripcion()));

        logger.debug(ObservacionItemAsesorKey.LOG_VERIFICACION_REGISTRAR, !contexto.esVacio(), descripcionYaExiste);
        validator.validar(registro, contexto, descripcionYaExiste);

        observacionItemAsesorOutputPort.registrar(ObservacionItemAsesorMapper.toEntity(observacion));
        logger.info(ObservacionItemAsesorKey.LOG_REGISTRADO, observacion.getId());

        return observacion.getId();
    }
}
