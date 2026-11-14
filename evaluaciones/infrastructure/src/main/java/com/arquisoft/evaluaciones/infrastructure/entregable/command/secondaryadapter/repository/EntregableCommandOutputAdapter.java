package com.arquisoft.evaluaciones.infrastructure.entregable.command.secondaryadapter.repository;

import com.arquisoft.evaluaciones.application.entregable.command.secondaryport.EntregableOutputPort;
import com.arquisoft.evaluaciones.application.entregable.command.secondaryport.entity.EntregableEntity;
import com.arquisoft.evaluaciones.infrastructure.entregable.command.secondaryadapter.mapper.EntregableJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EntregableKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableCommandOutputAdapter implements EntregableOutputPort {

    private final EntregableCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void guardar(EntregableEntity entregable) {
        repository.save(EntregableJpaMapper.toJpaEntity(entregable));
        logger.debug(EntregableKey.LOG_GUARDADO, entregable.id());
    }

    @Override
    public boolean existePorId(UUID id) {
        return repository.existsById(id);
    }
}
