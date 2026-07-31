package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaAggregate;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence.EstadoEvaluacionJpaRepository;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence.EstadoEvaluacionFichaJpaRepository;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence.EstadoEvaluacionFichaMapper;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoEvaluacionFichaCommandOutputAdapter implements EstadoEvaluacionFichaOutputPort {

    private final EstadoEvaluacionFichaJpaRepository estadoEvaluacionFichaJpaRepository;
    private final EstadoEvaluacionFichaMapper estadoEvaluacionFichaMapper;
    private final EvaluacionFichaPerfilJpaRepository evaluacionFichaPerfilJpaRepository;
    private final EstadoEvaluacionJpaRepository estadoEvaluacionJpaRepository;

    @Override
    public void guardar(EstadoEvaluacionFichaAggregate aggregate) {
        var evaluacionRef = evaluacionFichaPerfilJpaRepository
                .getReferenceById(aggregate.getEvaluacionFichaPerfilId());

        var entity = estadoEvaluacionFichaMapper.toEntity(aggregate, evaluacionRef);
        estadoEvaluacionFichaJpaRepository.save(entity);
    }

    @Override
    public boolean existePorEvaluacionYEstado(UUID evaluacionFichaPerfilId, String estadoEvaluacionId) {
        return estadoEvaluacionFichaJpaRepository
                .existsByEvaluacionFichaPerfilIdAndEstadoEvaluacionId(
                        evaluacionFichaPerfilId,
                        estadoEvaluacionId);
    }

    @Override
    public long contarEstadosPorEvaluacion(UUID evaluacionFichaPerfilId) {
        return estadoEvaluacionFichaJpaRepository.countByEvaluacionFichaPerfilId(evaluacionFichaPerfilId);
    }

    @Override
    public boolean existeEstadoEvaluacionPorId(String estadoEvaluacionId) {
        return estadoEvaluacionJpaRepository.existsById(estadoEvaluacionId);
    }

    @Override
    public Optional<String> obtenerUltimoEstado(UUID evaluacionFichaPerfilId) {
        return estadoEvaluacionFichaJpaRepository
                .findFirstByEvaluacionFichaPerfilIdOrderByFechaActualizacionDesc(evaluacionFichaPerfilId)
                .map(entity -> entity.getEstadoEvaluacion().getId());
    }
}
