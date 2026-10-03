package com.arquisoft.usuarios.infrastructure.bibliotecario.command.secondaryadapter.repository;

import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.usuarios.AgregarBibliotecarioKey;
import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.BibliotecarioOutputPort;
import com.arquisoft.usuarios.application.bibliotecario.command.secondaryport.entity.BibliotecarioEntity;
import com.arquisoft.usuarios.infrastructure.bibliotecario.command.secondaryadapter.mapper.BibliotecarioJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BibliotecarioCommandOutputAdapter implements BibliotecarioOutputPort {

    private final BibliotecarioCommandRepository bibliotecarioCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(BibliotecarioEntity bibliotecario) {
        bibliotecarioCommandRepository.save(BibliotecarioJpaMapper.toJpaEntity(bibliotecario));
        logger.debug(AgregarBibliotecarioKey.LOG_GUARDADO, bibliotecario.usuario());
    }

    @Override
    public void reactivar(UUID usuario) {
        bibliotecarioCommandRepository.reactivar(usuario);
        logger.debug(AgregarBibliotecarioKey.LOG_ACTUALIZADO, usuario);
    }

    @Override
    public Optional<BibliotecarioEntity> obtenerPorUsuario(UUID usuario) {
        return bibliotecarioCommandRepository.findById(usuario).map(BibliotecarioJpaMapper::toEntity);
    }
}
