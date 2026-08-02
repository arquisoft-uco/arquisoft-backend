package com.arquisoft.fichas.infrastructure.estadoevaluacionficha.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estadoevaluacionficha.aggregate.EstadoEvaluacionFichaAggregate;
import com.arquisoft.fichas.domain.estadoevaluacionficha.port.out.EstadoEvaluacionFichaOutputPort;
import com.arquisoft.fichas.infrastructure.estadoevaluacion.persistence.EstadoEvaluacionRepository;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence.EstadoEvaluacionFichaRepository;
import com.arquisoft.fichas.infrastructure.estadoevaluacionficha.persistence.EstadoEvaluacionFichaMapper;
import com.arquisoft.fichas.infrastructure.evaluacionfichaperfil.persistence.EvaluacionFichaPerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoEvaluacionFichaCommandOutputAdapter implements EstadoEvaluacionFichaOutputPort {

    private final EstadoEvaluacionFichaRepository estadoEvaluacionFichaRepository;
    private final EstadoEvaluacionFichaMapper estadoEvaluacionFichaMapper;
    private final EvaluacionFichaPerfilRepository evaluacionFichaPerfilRepository;
    private final EstadoEvaluacionRepository estadoEvaluacionRepository;

    @Override
    public void guardar(EstadoEvaluacionFichaAggregate aggregate) {
        var evaluacionRef = evaluacionFichaPerfilRepository
                .getReferenceById(aggregate.getEvaluacionFichaPerfilId());

        var entity = estadoEvaluacionFichaMapper.toEntity(aggregate, evaluacionRef);
        estadoEvaluacionFichaRepository.save(entity);
    }

    @Override
    public boolean existePorEvaluacionYEstado(UUID evaluacionFichaPerfilId, String estadoEvaluacionId) {
        return estadoEvaluacionFichaRepository
                .existsByEvaluacionFichaPerfilIdAndEstadoEvaluacionId(
                        evaluacionFichaPerfilId,
                        estadoEvaluacionId);
    }

    @Override
    public long contarEstadosPorEvaluacion(UUID evaluacionFichaPerfilId) {
        return estadoEvaluacionFichaRepository.countByEvaluacionFichaPerfilId(evaluacionFichaPerfilId);
    }

    @Override
    public boolean existeEstadoEvaluacionPorId(String estadoEvaluacionId) {
        return estadoEvaluacionRepository.existsById(estadoEvaluacionId);
    }

    @Override
    public Optional<String> obtenerUltimoEstado(UUID evaluacionFichaPerfilId) {
        return estadoEvaluacionFichaRepository
                .findFirstByEvaluacionFichaPerfilIdOrderByFechaActualizacionDesc(evaluacionFichaPerfilId)
                .map(entity -> entity.getEstadoEvaluacion().getId());
    }
}
