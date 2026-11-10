package com.arquisoft.entregables.infrastructure.paqueteentregable.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.entity.PaqueteEntregableEntity;
import com.arquisoft.entregables.infrastructure.paqueteentregable.command.secondaryadapter.mapper.PaqueteEntregableJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PaqueteEntregableCommandOutputAdapter implements PaqueteEntregableOutputPort {

    private final PaqueteEntregableCommandRepository paqueteEntregableCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(PaqueteEntregableEntity paquete) {
        paqueteEntregableCommandRepository.save(PaqueteEntregableJpaMapper.toJpaEntity(paquete));
        logger.debug(PaqueteEntregableKey.LOG_GUARDADO, paquete.id());
    }

    @Override
    public boolean existePorEntregable(UUID entregableProyectoGrado) {
        return paqueteEntregableCommandRepository.existsByEntregableProyectoGradoId(entregableProyectoGrado);
    }
}
