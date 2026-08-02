package com.arquisoft.fichas.infrastructure.representantecomite.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.representantecomite.port.out.RepresentanteComiteOutputPort;
import com.arquisoft.fichas.infrastructure.representantecomite.persistence.RepresentanteComiteJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RepresentanteComiteCommandOutputAdapter implements RepresentanteComiteOutputPort {

    private final RepresentanteComiteJpaRepository representanteComiteJpaRepository;

    @Override
    public boolean existePorId(UUID id) {
        return representanteComiteJpaRepository.existsById(id);
    }
}
