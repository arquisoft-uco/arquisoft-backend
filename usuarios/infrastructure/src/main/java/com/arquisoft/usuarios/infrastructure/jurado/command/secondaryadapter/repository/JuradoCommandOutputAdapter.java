package com.arquisoft.usuarios.infrastructure.jurado.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarJuradoKey;
import com.arquisoft.usuarios.application.jurado.command.secondaryport.JuradoOutputPort;
import com.arquisoft.usuarios.application.jurado.command.secondaryport.entity.JuradoEntity;
import com.arquisoft.usuarios.infrastructure.jurado.command.secondaryadapter.mapper.JuradoJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JuradoCommandOutputAdapter implements JuradoOutputPort {

    private final JuradoCommandRepository juradoCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(JuradoEntity jurado) {
        juradoCommandRepository.save(JuradoJpaMapper.toJpaEntity(jurado));
        logger.debug(AgregarJuradoKey.LOG_GUARDADO, jurado.usuario());
    }

    @Override
    public void eliminarLogica(UUID usuario, Instant eliminadoEn) {
        juradoCommandRepository.eliminarLogica(usuario, JuradoJpaMapper.aColumna(eliminadoEn));
        logger.debug(AgregarJuradoKey.LOG_ACTUALIZADO, usuario);
    }

    @Override
    public void reactivar(UUID usuario) {
        juradoCommandRepository.reactivar(usuario);
        logger.debug(AgregarJuradoKey.LOG_ACTUALIZADO, usuario);
    }

    @Override
    public Optional<JuradoEntity> obtenerPorUsuario(UUID usuario) {
        return juradoCommandRepository.findById(usuario).map(JuradoJpaMapper::toEntity);
    }
}
