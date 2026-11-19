package com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCualitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCuantitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ResumenEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.EvaluacionAsesorOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionasesor.command.secondaryport.entity.EvaluacionAsesorEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionasesor.command.secondaryadapter.mapper.EvaluacionAsesorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionAsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionAsesorCommandOutputAdapter implements EvaluacionAsesorOutputPort {

    private final EvaluacionAsesorCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrar(EvaluacionAsesorEntity evaluacionAsesor) {
        repository.save(EvaluacionAsesorJpaMapper.toJpaEntity(evaluacionAsesor));
        logger.debug(EvaluacionAsesorKey.LOG_GUARDADA, evaluacionAsesor.id());
    }

    @Override
    public Optional<EvaluacionAsesorEntity> obtenerPorEvaluacion(UUID evaluacion) {
        return repository.findByEvaluacion(evaluacion).map(EvaluacionAsesorJpaMapper::toEntity);
    }

    @Override
    public boolean existenRegistrosPorEvaluacion(UUID evaluacion) {
        return repository.existenRegistrosPorEvaluacion(evaluacion);
    }

    @Override
    public void actualizarAsesor(UUID evaluacionAsesor, UUID asesor) {
        repository.actualizarAsesor(evaluacionAsesor, asesor);
        logger.debug(EvaluacionAsesorKey.LOG_ASESOR_ACTUALIZADO, evaluacionAsesor, asesor);
    }

    @Override
    public boolean estaCompleta(UUID evaluacionAsesor) {
        return repository.estaCompleta(evaluacionAsesor);
    }

    @Override
    public List<ItemCualitativoCalificadoEntity> consultarCualitativosCalificados(UUID evaluacionAsesor) {
        return repository.findCualitativosCalificados(evaluacionAsesor).stream()
                .map(item -> new ItemCualitativoCalificadoEntity(item.getPeso(), item.getCriterio()))
                .toList();
    }

    @Override
    public List<ItemCuantitativoCalificadoEntity> consultarCuantitativosCalificados(UUID evaluacionAsesor) {
        return repository.findCuantitativosCalificados(evaluacionAsesor).stream()
                .map(item -> new ItemCuantitativoCalificadoEntity(item.getPuntaje(), item.getValorMaximo()))
                .toList();
    }

    @Override
    public void actualizarResumen(UUID evaluacionAsesor, ResumenEvaluacionEntity resumen) {
        repository.actualizarResumen(
                evaluacionAsesor,
                resumen.resumenCualitativo(),
                resumen.resumenCuantitativo(),
                resumen.notaAcumulada());
        logger.debug(EvaluacionAsesorKey.LOG_RESUMEN_ACTUALIZADO, evaluacionAsesor);
    }

    @Override
    public Optional<ResumenEvaluacionEntity> obtenerResumenPorEvaluacion(UUID evaluacion) {
        return repository.findByEvaluacion(evaluacion)
                .map(asesor -> new ResumenEvaluacionEntity(
                        asesor.getResumenCualitativo(), asesor.getResumenCuantitativo(), asesor.getNotaAcumulada()));
    }
}
