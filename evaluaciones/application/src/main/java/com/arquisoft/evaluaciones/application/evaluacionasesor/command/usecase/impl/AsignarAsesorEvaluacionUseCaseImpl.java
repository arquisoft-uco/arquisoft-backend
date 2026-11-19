package com.arquisoft.evaluaciones.application.evaluacionasesor.command.usecase.impl;

import com.arquisoft.evaluaciones.application.asesor.command.finder.AsesorVigenteExisteFinder;
import com.arquisoft.evaluaciones.application.evaluacion.command.finder.ContextoAsignacionEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.EvaluacionAsesorConRegistrosFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.finder.EvaluacionAsesorPorEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.mapper.EvaluacionAsesorMapper;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.usecase.AsignarAsesorEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.validator.AsignarAsesorEvaluacionValidator;
import com.arquisoft.evaluaciones.domain.asesor.model.ExistenciaAsesor;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoAsignacionEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.EvaluacionAsesorDomain;
import com.arquisoft.evaluaciones.domain.evaluacionasesor.model.CambioAsesorEvaluacion;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AsignarAsesorEvaluacionUseCaseImpl implements AsignarAsesorEvaluacionUseCase {

    private final ContextoAsignacionEvaluacionFinder contextoAsignacionEvaluacionFinder;
    private final AsesorVigenteExisteFinder asesorVigenteExisteFinder;
    private final EvaluacionAsesorPorEvaluacionFinder evaluacionAsesorPorEvaluacionFinder;
    private final EvaluacionAsesorConRegistrosFinder evaluacionAsesorConRegistrosFinder;
    private final AsignarAsesorEvaluacionValidator validator;
    private final EvaluacionAsesorOutputPort evaluacionAsesorOutputPort;
    private final AppLogger logger;

    @Override
    public void ejecutar(EvaluacionAsesorDomain evaluacionAsesor) {
        var evaluacion = evaluacionAsesor.getEvaluacion();
        var asesor = evaluacionAsesor.getAsesor();
        logger.info(EvaluacionAsesorKey.LOG_ASIGNANDO, evaluacion, asesor);

        var contexto = contextoAsignacionEvaluacionFinder.obtener(evaluacion);
        var asesorVigente = asesorVigenteExisteFinder.obtener(asesor);
        var actual = evaluacionAsesorPorEvaluacionFinder.obtener(evaluacion);
        var conRegistros = evaluacionAsesorConRegistrosFinder.obtener(evaluacion);
        logger.debug(EvaluacionAsesorKey.LOG_VERIFICACION_ASIGNAR,
                evaluacion, !contexto.esVacio(), asesorVigente, !actual.esVacio(), conRegistros);

        validator.validar(
                new ExistenciaEvaluacion(evaluacion, !contexto.esVacio()),
                new ExistenciaAsesor(asesor, asesorVigente),
                new EstadoAsignacionEvaluacion(contexto.getEstado()),
                new CambioAsesorEvaluacion(evaluacion, actual.getAsesor(), asesor, conRegistros));

        if (!actual.esVacio() && actual.tieneAsesor(asesor)) {
            logger.info(EvaluacionAsesorKey.LOG_SIN_CAMBIOS, evaluacion, asesor);
            return;
        }

        if (actual.esVacio()) {
            evaluacionAsesorOutputPort.registrar(EvaluacionAsesorMapper.toEntity(evaluacionAsesor));
        } else {
            evaluacionAsesorOutputPort.actualizarAsesor(actual.getId(), asesor);
        }

        logger.info(EvaluacionAsesorKey.LOG_ASIGNADA, evaluacion, asesor);
    }
}
