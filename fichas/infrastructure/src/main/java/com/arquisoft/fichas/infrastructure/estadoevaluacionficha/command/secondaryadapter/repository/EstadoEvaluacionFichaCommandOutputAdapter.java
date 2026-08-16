package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.application.estadoevaluacionficha.command.secondaryport.entity.EstadoEvaluacionFichaEntity;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.command.secondaryadapter.repository.EstadoEvaluacionCommandRepository;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.secondaryadapter.mapper.EstadoEvaluacionFichaJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoEvaluacionFichaCommandOutputAdapter implements EstadoEvaluacionFichaOutputPort {

    private final EstadoEvaluacionFichaCommandRepository estadoEvaluacionFichaCommandRepository;
    private final EstadoEvaluacionCommandRepository estadoEvaluacionCommandRepository;

    @Override
    public void registrarEstadoInicial(EstadoEvaluacionFichaEntity estado) {
        estadoEvaluacionFichaCommandRepository.save(EstadoEvaluacionFichaJpaMapper.toJpaEntity(estado));
    }

    @Override
    public void agregarEstado(EstadoEvaluacionFichaEntity estado) {
        estadoEvaluacionFichaCommandRepository.save(EstadoEvaluacionFichaJpaMapper.toJpaEntity(estado));
    }

    @Override
    public boolean existePorEvaluacionYEstado(UUID evaluacionFichaPerfilId, String estadoEvaluacionId) {
        return estadoEvaluacionFichaCommandRepository
                .existsByEvaluacionFichaPerfilAndEstadoEvaluacion(
                        evaluacionFichaPerfilId,
                        estadoEvaluacionId);
    }

    @Override
    public long contarEstadosPorEvaluacion(UUID evaluacionFichaPerfilId) {
        return estadoEvaluacionFichaCommandRepository
                .countByEvaluacionFichaPerfil(evaluacionFichaPerfilId);
    }

    @Override
    public boolean existeEstadoEvaluacionPorId(String estadoEvaluacionId) {
        return estadoEvaluacionCommandRepository.existsById(estadoEvaluacionId);
    }

    @Override
    public Optional<EstadoEvaluacionFichaEntity> obtenerUltimoEstado(UUID evaluacionFichaPerfilId) {
        return estadoEvaluacionFichaCommandRepository
                .findFirstByEvaluacionFichaPerfilIdOrderByFechaActualizacionDesc(evaluacionFichaPerfilId)
                .map(EstadoEvaluacionFichaJpaMapper::toEntity);
    }
}
