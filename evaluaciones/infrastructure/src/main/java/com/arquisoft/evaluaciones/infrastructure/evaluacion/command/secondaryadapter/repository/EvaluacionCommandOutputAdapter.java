package com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.EvaluacionOutputPort;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.AvanceEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ConsolidacionNotasEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ContextoAsignacionEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.EvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.FinalizacionEvaluacionEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.mapper.AvanceEvaluacionJpaMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.mapper.ContextoAsignacionEvaluacionJpaMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacion.command.secondaryadapter.mapper.EvaluacionJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionCommandOutputAdapter implements EvaluacionOutputPort {

    private final EvaluacionCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrar(EvaluacionEntity evaluacion) {
        repository.save(EvaluacionJpaMapper.toJpaEntity(evaluacion));
        logger.debug(EvaluacionKey.LOG_GUARDADA, evaluacion.id());
    }

    @Override
    public boolean existePorEntregable(UUID entregable) {
        return repository.existsByEntregable(entregable);
    }

    @Override
    public void actualizarEstado(UUID evaluacion, String estado) {
        repository.actualizarEstado(evaluacion, estado);
        logger.debug(EvaluacionKey.LOG_ESTADO_ACTUALIZADO, evaluacion, estado);
    }

    @Override
    public Optional<ContextoAsignacionEvaluacionEntity> obtenerContextoAsignacionBloqueado(UUID evaluacion) {
        return repository.buscarContextoAsignacionBloqueado(evaluacion)
                .map(ContextoAsignacionEvaluacionJpaMapper::toEntity);
    }

    @Override
    public void actualizarNotas(ConsolidacionNotasEvaluacionEntity consolidacion) {
        repository.actualizarNotas(
                consolidacion.evaluacion(),
                consolidacion.notaAsesor(),
                consolidacion.notaJurados(),
                consolidacion.pesoAsesor(),
                consolidacion.pesoJurados(),
                consolidacion.cantidadJurados());
        logger.debug(EvaluacionKey.LOG_NOTAS_ACTUALIZADAS, consolidacion.evaluacion());
    }

    @Override
    public Optional<AvanceEvaluacionEntity> obtenerAvance(UUID evaluacion) {
        return repository.buscarAvance(evaluacion).map(AvanceEvaluacionJpaMapper::toEntity);
    }

    @Override
    public boolean existenObservaciones(UUID evaluacion) {
        return repository.existenObservaciones(evaluacion);
    }

    @Override
    public void finalizar(FinalizacionEvaluacionEntity finalizacion) {
        repository.finalizar(
                finalizacion.evaluacion(),
                finalizacion.estado(),
                finalizacion.notaFinal(),
                finalizacion.resultado());
        logger.debug(EvaluacionKey.LOG_FINALIZACION_GUARDADA, finalizacion.evaluacion(), finalizacion.resultado());
    }
}
