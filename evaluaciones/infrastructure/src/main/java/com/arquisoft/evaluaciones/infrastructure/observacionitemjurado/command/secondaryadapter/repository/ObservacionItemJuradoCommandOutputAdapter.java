package com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.ObservacionItemJuradoOutputPort;
import com.arquisoft.evaluaciones.application.observacionitemjurado.command.secondaryport.entity.ObservacionItemJuradoEntity;
import com.arquisoft.evaluaciones.infrastructure.observacionitemjurado.command.secondaryadapter.mapper.ObservacionItemJuradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.ObservacionItemJuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ObservacionItemJuradoCommandOutputAdapter implements ObservacionItemJuradoOutputPort {

    private final ObservacionItemJuradoCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrar(ObservacionItemJuradoEntity entity) {
        repository.save(ObservacionItemJuradoJpaMapper.toJpaEntity(entity));
        logger.debug(ObservacionItemJuradoKey.LOG_GUARDADA, entity.id());
    }

    @Override
    public boolean existePorEvaluacionYDescripcion(UUID evaluacionCuantitativaJurado, String descripcion) {
        return repository.existsByEvaluacionCuantitativaJuradoIdAndDescripcion(evaluacionCuantitativaJurado, descripcion);
    }

    @Override
    public Optional<ObservacionItemJuradoEntity> obtenerPorId(UUID id) {
        return repository.findById(id).map(ObservacionItemJuradoJpaMapper::toEntity);
    }

    @Override
    public boolean existeOtraConDescripcion(UUID observacion, String descripcion) {
        return repository.existeOtraConDescripcion(observacion, descripcion);
    }

    @Override
    public void actualizarDescripcion(UUID id, String descripcion) {
        repository.actualizarDescripcion(id, descripcion);
        logger.debug(ObservacionItemJuradoKey.LOG_GUARDADA, id);
    }
}
