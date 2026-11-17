package com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.DocumentacionOutputPort;
import com.arquisoft.biblioteca.application.documentacion.command.secondaryport.entity.DocumentacionEntity;
import com.arquisoft.biblioteca.infrastructure.documentacion.command.secondaryadapter.mapper.DocumentacionJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.DocumentacionKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DocumentacionCommandOutputAdapter implements DocumentacionOutputPort {

    private final DocumentacionCommandRepository documentacionCommandRepository;
    private final AppLogger logger;

    @Override
    public Optional<DocumentacionEntity> obtenerPorId(UUID id) {
        return documentacionCommandRepository.buscarBloqueadaPorId(id).map(DocumentacionJpaMapper::toEntity);
    }

    @Override
    public boolean existePorEntregableProyectoGrado(UUID entregableProyectoGrado) {
        return documentacionCommandRepository.existsByEntregableProyectoGradoId(entregableProyectoGrado);
    }

    @Override
    public void registrar(DocumentacionEntity documentacion) {
        documentacionCommandRepository.save(DocumentacionJpaMapper.toJpaEntity(documentacion));
        logger.debug(DocumentacionKey.LOG_GUARDADA, documentacion.id());
    }

    @Override
    public void actualizarEstado(UUID documentacion, String estadoDocumentacion) {
        documentacionCommandRepository.actualizarEstado(documentacion, estadoDocumentacion);
        logger.debug(DocumentacionKey.LOG_ESTADO_ACTUALIZADO, documentacion);
    }
}
