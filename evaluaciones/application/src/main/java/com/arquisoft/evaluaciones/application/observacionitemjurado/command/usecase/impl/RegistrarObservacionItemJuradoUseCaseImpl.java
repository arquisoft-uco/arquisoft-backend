package com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.EvaluacionCuantitativaJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoFinalizadaFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.DescripcionObservacionItemJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.model.CriterioDescripcionObservacionItemJurado;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.mapper.ObservacionItemJuradoMapper;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.RegistrarObservacionItemJuradoUseCase;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.RegistrarObservacionItemJuradoValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.ObservacionItemJuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RegistrarObservacionItemJuradoUseCaseImpl implements RegistrarObservacionItemJuradoUseCase {

    private final ObservacionItemJuradoOutputPort observacionItemJuradoOutputPort;
    private final EvaluacionCuantitativaJuradoPorIdFinder evaluacionCuantitativaJuradoPorIdFinder;
    private final EvaluacionJuradoFinalizadaFinder evaluacionJuradoFinalizadaFinder;
    private final DescripcionObservacionItemJuradoExisteFinder descripcionObservacionItemJuradoExisteFinder;
    private final RegistrarObservacionItemJuradoValidator validator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(ObservacionItemJuradoDomain observacion) {
        logger.info(ObservacionItemJuradoKey.LOG_REGISTRANDO, observacion.getEvaluacionCuantitativaJurado());

        var evaluacion = evaluacionCuantitativaJuradoPorIdFinder
                .obtener(observacion.getEvaluacionCuantitativaJurado())
                .orElse(EvaluacionCuantitativaJuradoDomain.VACIO);
        var finalizada = evaluacionJuradoFinalizadaFinder.obtener(evaluacion.getEvaluacionJurado());
        var descripcionYaExiste = descripcionObservacionItemJuradoExisteFinder.obtener(
                new CriterioDescripcionObservacionItemJurado(
                        observacion.getEvaluacionCuantitativaJurado(), observacion.getDescripcion()));

        logger.debug(ObservacionItemJuradoKey.LOG_VERIFICACION_REGISTRAR,
                !evaluacion.esVacio(), finalizada, descripcionYaExiste);
        validator.validar(observacion, evaluacion, finalizada, descripcionYaExiste);

        observacionItemJuradoOutputPort.registrar(ObservacionItemJuradoMapper.toEntity(observacion));
        logger.info(ObservacionItemJuradoKey.LOG_REGISTRADO, observacion.getId());

        return observacion.getId();
    }
}
