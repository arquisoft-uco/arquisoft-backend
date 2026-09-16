package com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacioncuantitativajurado.command.finder.EvaluacionCuantitativaJuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.EvaluacionJuradoEstadoFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model.SolicitudEstadoEvaluacionJurado;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.DescripcionObservacionItemJuradoExisteFinder;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.finder.model.CriterioDescripcionObservacionItemJurado;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.mapper.ObservacionItemJuradoMapper;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.usecase.RegistrarObservacionItemJuradoUseCase;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.validator.RegistrarObservacionItemJuradoValidator;
import com.arquisoft.evaluaciones.domain.evaluacioncuantitativajurado.EvaluacionCuantitativaJuradoDomain;
import com.arquisoft.evaluaciones.domain.observacionitemjurado.RegistroObservacionItemJuradoDomain;
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
    private final EvaluacionJuradoEstadoFinder evaluacionJuradoEstadoFinder;
    private final DescripcionObservacionItemJuradoExisteFinder descripcionObservacionItemJuradoExisteFinder;
    private final RegistrarObservacionItemJuradoValidator validator;
    private final AppLogger logger;

    @Override
    public UUID ejecutar(RegistroObservacionItemJuradoDomain registro) {
        var observacion = registro.getObservacion();
        logger.info(ObservacionItemJuradoKey.LOG_REGISTRANDO, observacion.getEvaluacionCuantitativaJurado());

        var evaluacion = evaluacionCuantitativaJuradoPorIdFinder
                .obtener(observacion.getEvaluacionCuantitativaJurado())
                .orElse(EvaluacionCuantitativaJuradoDomain.VACIO);
        var estado = evaluacionJuradoEstadoFinder.obtener(
                new SolicitudEstadoEvaluacionJurado(evaluacion.getEvaluacionJurado(), registro.getJurado()));
        var descripcionYaExiste = descripcionObservacionItemJuradoExisteFinder.obtener(
                new CriterioDescripcionObservacionItemJurado(
                        observacion.getEvaluacionCuantitativaJurado(), observacion.getDescripcion()));

        logger.debug(ObservacionItemJuradoKey.LOG_VERIFICACION_REGISTRAR,
                !evaluacion.esVacio(), estado.pertenece(), estado.finalizada(), descripcionYaExiste);
        validator.validar(observacion, evaluacion, estado, descripcionYaExiste);

        observacionItemJuradoOutputPort.registrar(ObservacionItemJuradoMapper.toEntity(observacion));
        logger.info(ObservacionItemJuradoKey.LOG_REGISTRADO, observacion.getId());

        return observacion.getId();
    }
}
