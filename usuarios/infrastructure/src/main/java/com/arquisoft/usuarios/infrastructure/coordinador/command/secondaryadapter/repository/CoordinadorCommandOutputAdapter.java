package com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.repository;

import com.arquisoft.usuarios.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.usuarios.application.coordinador.command.secondaryport.entity.CoordinadorEntity;
import com.arquisoft.usuarios.infrastructure.coordinador.command.secondaryadapter.mapper.CoordinadorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarCoordinadorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CoordinadorCommandOutputAdapter implements CoordinadorOutputPort {

    private final CoordinadorCommandRepository coordinadorCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(CoordinadorEntity coordinador) {
        coordinadorCommandRepository.save(CoordinadorJpaMapper.toJpaEntity(coordinador));
        logger.debug(AgregarCoordinadorKey.LOG_GUARDADO, coordinador.usuario());
    }

    @Override
    public void eliminarLogica(UUID usuario, Instant eliminadoEn) {
        coordinadorCommandRepository.eliminarLogica(usuario, CoordinadorJpaMapper.aColumna(eliminadoEn));
        logger.debug(AgregarCoordinadorKey.LOG_ACTUALIZADO, usuario);
    }

    @Override
    public void reactivar(UUID usuario) {
        coordinadorCommandRepository.reactivar(usuario);
        logger.debug(AgregarCoordinadorKey.LOG_ACTUALIZADO, usuario);
    }

    @Override
    public Optional<CoordinadorEntity> obtenerPorUsuario(UUID usuario) {
        return coordinadorCommandRepository.findById(usuario).map(CoordinadorJpaMapper::toEntity);
    }
}
