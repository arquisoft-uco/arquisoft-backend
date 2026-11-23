package com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.biblioteca.infrastructure.entregableproyectogrado.command.secondaryadapter.mapper.EntregableProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EntregableProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableProyectoGradoCommandOutputAdapter implements EntregableProyectoGradoOutputPort {

    private final EntregableProyectoGradoCommandRepository entregableProyectoGradoCommandRepository;
    private final AppLogger logger;

    @Override
    public Optional<EntregableProyectoGradoEntity> obtenerBloqueadoPorId(UUID id) {
        return entregableProyectoGradoCommandRepository.buscarBloqueadoPorId(id)
                .map(EntregableProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public boolean existePorId(UUID id) {
        return entregableProyectoGradoCommandRepository.existsById(id);
    }

    @Override
    public void guardar(EntregableProyectoGradoEntity entregable) {
        entregableProyectoGradoCommandRepository.save(EntregableProyectoGradoJpaMapper.toJpaEntity(entregable));
        logger.debug(EntregableProyectoGradoKey.LOG_GUARDADO, entregable.id());
    }
}
