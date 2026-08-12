package com.arquisoft.fichas.infrastructure.representantecomite.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.representantecomite.query.secondaryport.RepresentanteComiteQueryOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RepresentanteComiteQueryOutputAdapter
        implements RepresentanteComiteQueryOutputPort {

    private final RepresentanteComiteQueryRepository repository;

    @Override
    public boolean existePorId(UUID id) {
        return repository.existsById(id);
    }
}
