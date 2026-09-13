package com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.usuarios.application.estudiante.command.secondaryport.entity.EstudianteEntity;
import com.arquisoft.usuarios.infrastructure.estudiante.command.secondaryadapter.mapper.EstudianteJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarEstudianteKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteCommandOutputAdapter implements EstudianteOutputPort {

    private final EstudianteCommandRepository estudianteCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(EstudianteEntity estudiante) {
        estudianteCommandRepository.save(EstudianteJpaMapper.toJpaEntity(estudiante));
        logger.debug(AgregarEstudianteKey.LOG_GUARDADO, estudiante.usuario());
    }

    @Override
    public boolean existePorUsuario(UUID usuario) {
        return estudianteCommandRepository.existsById(usuario);
    }
}
