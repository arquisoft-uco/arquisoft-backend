package com.arquisoft.biblioteca.infrastructure.documentacion.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.documentacion.query.secondaryport.DocumentacionAccesoQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class DocumentacionAccesoQueryOutputAdapter implements DocumentacionAccesoQueryOutputPort {

    private final DocumentacionQueryRepository documentacionQueryRepository;

    @Override
    public Optional<String> obtenerEstadoPorId(UUID documentacion) {
        return documentacionQueryRepository.findById(documentacion)
                .map(DocumentacionJpaQueryEntity::getEstadoId);
    }
}
