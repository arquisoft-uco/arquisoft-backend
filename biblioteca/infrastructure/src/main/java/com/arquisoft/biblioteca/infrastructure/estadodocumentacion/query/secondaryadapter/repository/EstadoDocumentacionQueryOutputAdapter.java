package com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.estadodocumentacion.query.readmodel.EstadoDocumentacionReadModel;
import com.arquisoft.biblioteca.application.estadodocumentacion.query.secondaryport.EstadoDocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.infrastructure.estadodocumentacion.query.secondaryadapter.repository.mapper.EstadoDocumentacionQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstadoDocumentacionQueryOutputAdapter implements EstadoDocumentacionQueryOutputPort {

    private final EstadoDocumentacionQueryRepository repository;

    @Override
    public List<EstadoDocumentacionReadModel> findAll() {
        return repository.findAll()
                .stream()
                .map(EstadoDocumentacionQueryMapper::toReadModel)
                .toList();
    }
}
