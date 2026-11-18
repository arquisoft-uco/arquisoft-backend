package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.ArchivoDocumentacionAccesoQueryOutputPort;
import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.entity.ArchivoDocumentacionAccesoEntity;
import com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository.mapper.ArchivoDocumentacionAccesoQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ArchivoDocumentacionAccesoQueryOutputAdapter implements ArchivoDocumentacionAccesoQueryOutputPort {

    private final ArchivoDocumentacionQueryRepository archivoDocumentacionQueryRepository;

    @Override
    public Optional<ArchivoDocumentacionAccesoEntity> obtenerPorId(UUID documentacion) {
        return archivoDocumentacionQueryRepository.findById(documentacion)
                .map(ArchivoDocumentacionAccesoQueryMapper::toEntity);
    }
}
