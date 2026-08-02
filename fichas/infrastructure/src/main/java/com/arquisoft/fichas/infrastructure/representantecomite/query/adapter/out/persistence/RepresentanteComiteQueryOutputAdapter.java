package com.arquisoft.fichas.infrastructure.representantecomite.query.adapter.out.persistence;

import com.arquisoft.fichas.application.representantecomite.query.port.out.RepresentanteComiteQueryOutputPort;
import com.arquisoft.fichas.infrastructure.representantecomite.persistence.RepresentanteComiteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RepresentanteComiteQueryOutputAdapter
        implements RepresentanteComiteQueryOutputPort {

    private final RepresentanteComiteRepository jpaRepository;

    @Override
    public boolean existePorId(UUID id) {
        return jpaRepository.existsById(id);
    }
}
