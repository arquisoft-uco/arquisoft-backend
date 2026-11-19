package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCualitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ItemCuantitativoCalificadoEntity;
import com.arquisoft.evaluaciones.application.evaluacion.command.secondaryport.entity.ResumenEvaluacionEntity;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.mapper.EstadoEvaluacionJuradoJpaMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.mapper.EvaluacionJuradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EvaluacionJuradoCommandOutputAdapter implements EvaluacionJuradoOutputPort {

    private final EvaluacionJuradoCommandRepository repository;
    private final AppLogger logger;

    @Override
    public Optional<EstadoEvaluacionJuradoEntity> obtenerEstadoBloqueado(UUID evaluacionJurado) {
        return repository.buscarEstadoBloqueado(evaluacionJurado)
                .map(EstadoEvaluacionJuradoJpaMapper::toEntity);
    }

    @Override
    public void registrarTodas(List<EvaluacionJuradoEntity> entidades) {
        var jpaEntidades = entidades.stream()
                .map(EvaluacionJuradoJpaMapper::toJpaEntity)
                .toList();
        repository.saveAll(jpaEntidades);
        logger.debug(EvaluacionJuradoKey.LOG_LOTE_GUARDADO, entidades.size());
    }

    @Override
    public void actualizarEstado(UUID evaluacionJurado, String estado) {
        repository.actualizarEstado(evaluacionJurado, estado);
        logger.debug(EvaluacionJuradoKey.LOG_ESTADO_ACTUALIZADO, evaluacionJurado, estado);
    }

    @Override
    public boolean estaCompleta(UUID evaluacionJurado) {
        return repository.estaCompleta(evaluacionJurado);
    }

    @Override
    public Set<UUID> consultarJuradosAsignados(UUID evaluacion, Set<UUID> jurados) {
        return repository.findJuradosAsignados(evaluacion, jurados);
    }

    @Override
    public Set<UUID> consultarJuradosEvaluados(UUID evaluacion, Set<UUID> jurados) {
        return repository.findJuradosEvaluados(evaluacion, jurados);
    }

    @Override
    public void marcarEliminadosPorEvaluacionYJurados(UUID evaluacion, Set<UUID> jurados, Instant eliminadoEn) {
        repository.marcarEliminadosPorEvaluacionYJurados(evaluacion, jurados, eliminadoEn);
        logger.debug(EvaluacionJuradoKey.LOG_LOTE_ELIMINADO, jurados.size());
    }

    @Override
    public boolean estaFinalizada(UUID evaluacionCuantitativaJurado) {
        return repository.estaFinalizada(evaluacionCuantitativaJurado).orElse(false);
    }

    @Override
    public boolean estaFinalizadaPorObservacion(UUID observacion) {
        return repository.estaFinalizadaPorObservacion(observacion).orElse(false);
    }

    @Override
    public List<ItemCualitativoCalificadoEntity> consultarCualitativosCalificados(UUID evaluacionJurado) {
        return repository.findCualitativosCalificados(evaluacionJurado).stream()
                .map(item -> new ItemCualitativoCalificadoEntity(item.getPeso(), item.getCriterio()))
                .toList();
    }

    @Override
    public List<ItemCuantitativoCalificadoEntity> consultarCuantitativosCalificados(UUID evaluacionJurado) {
        return repository.findCuantitativosCalificados(evaluacionJurado).stream()
                .map(item -> new ItemCuantitativoCalificadoEntity(item.getPuntaje(), item.getValorMaximo()))
                .toList();
    }

    @Override
    public void actualizarResumen(UUID evaluacionJurado, ResumenEvaluacionEntity resumen) {
        repository.actualizarResumen(
                evaluacionJurado,
                resumen.resumenCualitativo(),
                resumen.resumenCuantitativo(),
                resumen.notaAcumulada());
        logger.debug(EvaluacionJuradoKey.LOG_RESUMEN_ACTUALIZADO, evaluacionJurado);
    }

    @Override
    public List<ResumenEvaluacionEntity> consultarResumenesPorEvaluacion(UUID evaluacion) {
        return repository.findConNotaVigentesPorEvaluacion(evaluacion).stream()
                .map(jurado -> new ResumenEvaluacionEntity(
                        jurado.getResumenCualitativo(), jurado.getResumenCuantitativo(), jurado.getNotaAcumulada()))
                .toList();
    }
}
