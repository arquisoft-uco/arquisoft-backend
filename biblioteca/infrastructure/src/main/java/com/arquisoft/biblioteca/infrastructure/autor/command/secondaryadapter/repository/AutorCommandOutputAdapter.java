package com.arquisoft.biblioteca.infrastructure.autor.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.autor.command.secondaryport.AutorOutputPort;
import com.arquisoft.biblioteca.application.autor.command.secondaryport.entity.AutorEntity;
import com.arquisoft.biblioteca.infrastructure.autor.command.secondaryadapter.mapper.AutorJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.AutorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AutorCommandOutputAdapter implements AutorOutputPort {

    private final AutorCommandRepository autorCommandRepository;
    private final AppLogger logger;

    @Override
    public void guardar(AutorEntity autor) {
        autorCommandRepository.save(AutorJpaMapper.toJpaEntity(autor));
        logger.debug(AutorKey.LOG_GUARDADO, autor.id());
    }

    @Override
    public void reactivar(AutorEntity autor) {
        autorCommandRepository.reactivar(autor.id(), autor.identificador(),
                autor.nombre(), autor.email(), autor.ocurridoEn());
        logger.debug(AutorKey.LOG_ACTUALIZADO, autor.id());
    }

    @Override
    public void eliminarLogica(UUID id, Instant ocurridoEn) {
        autorCommandRepository.eliminarLogica(id, ocurridoEn);
        logger.debug(AutorKey.LOG_ACTUALIZADO, id);
    }

    @Override
    public Optional<AutorEntity> obtenerPorId(UUID id) {
        return autorCommandRepository.findById(id).map(AutorJpaMapper::toEntity);
    }
}
