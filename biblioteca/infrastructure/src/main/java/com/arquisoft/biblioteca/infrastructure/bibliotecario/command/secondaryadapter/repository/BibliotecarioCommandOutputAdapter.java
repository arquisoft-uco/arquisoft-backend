package com.arquisoft.biblioteca.infrastructure.bibliotecario.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.BibliotecarioOutputPort;
import com.arquisoft.biblioteca.application.bibliotecario.command.secondaryport.entity.BibliotecarioEntity;
import com.arquisoft.biblioteca.infrastructure.bibliotecario.command.secondaryadapter.mapper.BibliotecarioJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.BibliotecarioKey;
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
        logger.debug(BibliotecarioKey.LOG_GUARDADO, bibliotecario.id());
    }

    @Override
    public void reactivar(BibliotecarioEntity bibliotecario) {
        bibliotecarioCommandRepository.reactivar(bibliotecario.id(), bibliotecario.identificador(),
                bibliotecario.nombre(), bibliotecario.email(), bibliotecario.ocurridoEn());
        logger.debug(BibliotecarioKey.LOG_ACTUALIZADO, bibliotecario.id());
    }

    @Override
    public Optional<BibliotecarioEntity> obtenerPorId(UUID id) {
        return bibliotecarioCommandRepository.findById(id).map(BibliotecarioJpaMapper::toEntity);
    }
}
