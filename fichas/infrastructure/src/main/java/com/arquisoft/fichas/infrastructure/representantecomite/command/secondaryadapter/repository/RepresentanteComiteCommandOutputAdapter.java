package com.arquisoft.fichas.infrastructure.representantecomite.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.representantecomite.command.secondaryport.RepresentanteComiteOutputPort;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RepresentanteComiteCommandOutputAdapter implements RepresentanteComiteOutputPort {

    private final RepresentanteComiteCommandRepository representanteComiteRepository;

    @Override
    public boolean existePorId(UUID id) {
        return representanteComiteRepository.existsById(id);
    }
}
