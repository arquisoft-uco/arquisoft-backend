package com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.secondaryadapter.repository;

import com.arquisoft.biblioteca.application.autordocumentacion.query.readmodel.AutorDocumentacionReadModel;
import com.arquisoft.biblioteca.application.autordocumentacion.query.secondaryport.AutorDocumentacionQueryOutputPort;
import com.arquisoft.biblioteca.infrastructure.autordocumentacion.query.secondaryadapter.repository.mapper.AutorDocumentacionQueryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AutorDocumentacionQueryOutputAdapter implements AutorDocumentacionQueryOutputPort {

    private final AutorDocumentacionQueryRepository autorDocumentacionQueryRepository;

    @Override
    public List<AutorDocumentacionReadModel> consultarPorDocumentacion(UUID documentacion) {
        return autorDocumentacionQueryRepository.findByDocumentacionIdOrderByNombreAscIdAsc(documentacion).stream()
                .map(AutorDocumentacionQueryMapper::toReadModel)
                .toList();
    }
}
