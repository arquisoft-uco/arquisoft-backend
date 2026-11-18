package com.arquisoft.biblioteca.infrastructure.autordocumentacion.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.autordocumentacion.command.secondaryport.AutorDocumentacionOutputPort;
import com.arquisoft.biblioteca.application.autordocumentacion.command.secondaryport.entity.AutorDocumentacionEntity;
import com.arquisoft.biblioteca.infrastructure.autordocumentacion.command.secondaryadapter.mapper.AutorDocumentacionJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.AutorDocumentacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AutorDocumentacionCommandOutputAdapter implements AutorDocumentacionOutputPort {

    private final AutorDocumentacionCommandRepository autorDocumentacionCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrarTodos(List<AutorDocumentacionEntity> autores) {
        autorDocumentacionCommandRepository.saveAll(
                autores.stream().map(AutorDocumentacionJpaMapper::toJpaEntity).toList());
        logger.debug(AutorDocumentacionKey.LOG_GUARDADOS, autores.size());
    }
}
