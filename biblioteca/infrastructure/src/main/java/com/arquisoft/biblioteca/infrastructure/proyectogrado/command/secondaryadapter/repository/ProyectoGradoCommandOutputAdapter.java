package com.arquisoft.biblioteca.infrastructure.proyectogrado.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.proyectogrado.command.secondaryport.entity.ProyectoGradoEntity;
import com.arquisoft.biblioteca.infrastructure.proyectogrado.command.secondaryadapter.mapper.ProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ProyectoGradoCommandOutputAdapter implements ProyectoGradoOutputPort {

    private final ProyectoGradoCommandRepository proyectoGradoCommandRepository;
    private final AppLogger logger;

    @Override
    public Optional<ProyectoGradoEntity> obtenerPorId(UUID id) {
        return proyectoGradoCommandRepository.findById(id).map(ProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public void guardar(ProyectoGradoEntity proyecto) {
        proyectoGradoCommandRepository.save(ProyectoGradoJpaMapper.toJpaEntity(proyecto));
        logger.debug(ProyectoGradoKey.LOG_GUARDADO, proyecto.id());
    }
}
