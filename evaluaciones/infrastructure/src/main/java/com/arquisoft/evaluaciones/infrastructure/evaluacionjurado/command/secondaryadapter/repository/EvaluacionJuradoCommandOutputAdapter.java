package com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.EvaluacionJuradoOutputPort;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EstadoEvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.application.evaluacionjurado.command.secondaryport.entity.EvaluacionJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.mapper.EstadoEvaluacionJuradoJpaMapper;
import com.arquisoft.evaluaciones.infrastructure.evaluacionjurado.command.secondaryadapter.mapper.EvaluacionJuradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EvaluacionJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

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
    public Set<UUID> consultarJuradosAsignados(UUID evaluacion, Set<UUID> jurados) {
        return repository.findJuradosAsignados(evaluacion, jurados);
    }

    @Override
    public boolean estaFinalizada(UUID evaluacionJurado) {
        return repository.estaFinalizada(evaluacionJurado).orElse(false);
    }
}
