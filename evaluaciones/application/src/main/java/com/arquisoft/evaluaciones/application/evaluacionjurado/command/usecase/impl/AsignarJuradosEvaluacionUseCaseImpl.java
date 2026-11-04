package com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.evaluacion.command.finder.ContextoAsignacionEvaluacionFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.JuradosAsignadosFinder;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.finder.model.CriterioJuradosEvaluacion;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.mapper.EvaluacionJuradoMapper;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.usecase.AsignarJuradosEvaluacionUseCase;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.validator.AsignarJuradosEvaluacionValidator;
import com.arquisoft.evaluaciones.application.jurado.command.finder.JuradosVigentesFinder;
import com.arquisoft.evaluaciones.domain.evaluacion.model.EstadoRegistroEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacion.model.ExistenciaEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.AsignacionJuradosEvaluacionDomain;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.EvaluacionJuradoDomain;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.DisponibilidadJuradosEvaluacion;
import com.arquisoft.evaluaciones.domain.evaluacionjurado.model.ExistenciaJurados;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsignarJuradosEvaluacionUseCaseImpl implements AsignarJuradosEvaluacionUseCase {

    private final ContextoAsignacionEvaluacionFinder contextoAsignacionEvaluacionFinder;
    private final JuradosVigentesFinder juradosVigentesFinder;
    private final JuradosAsignadosFinder juradosAsignadosFinder;
    private final AsignarJuradosEvaluacionValidator validator;
    private final EvaluacionJuradoOutputPort evaluacionJuradoOutputPort;
    private final AppLogger logger;

    @Override
    public List<UUID> ejecutar(AsignacionJuradosEvaluacionDomain asignacion) {
        var evaluacion = asignacion.getEvaluacion();
        var solicitados = asignacion.getJurados();
        logger.info(EvaluacionJuradoKey.LOG_ASIGNANDO, evaluacion, solicitados.size());

        var contexto = contextoAsignacionEvaluacionFinder.obtener(evaluacion);
        var vigentes = juradosVigentesFinder.obtener(solicitados);
        var yaAsignados = juradosAsignadosFinder.obtener(new CriterioJuradosEvaluacion(evaluacion, solicitados));
        logger.debug(EvaluacionJuradoKey.LOG_VERIFICACION_ASIGNAR,
                evaluacion, !contexto.esVacio(), vigentes.size(), yaAsignados.size());

        validator.validar(
                new ExistenciaEvaluacion(evaluacion, !contexto.esVacio()),
                new EstadoRegistroEvaluacion(contexto.getEstado()),
                new ExistenciaJurados(solicitados, vigentes),
                new DisponibilidadJuradosEvaluacion(evaluacion, solicitados, yaAsignados));

        var entidades = asignacion.getJuradosEvaluacion().stream()
                .map(EvaluacionJuradoMapper::toEntity)
                .toList();
        evaluacionJuradoOutputPort.registrarTodas(entidades);

        logger.info(EvaluacionJuradoKey.LOG_ASIGNADOS, evaluacion, entidades.size());
        return asignacion.getJuradosEvaluacion().stream()
                .map(EvaluacionJuradoDomain::getId)
                .toList();
    }
}
