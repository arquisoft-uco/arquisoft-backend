package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.asesorficha.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaCommandOutputAdapter implements AsesorFichaOutputPort {

    private final AsesorFichaRepository asesorFichaRepository;

    @Override
    public boolean existePorId(UUID id) {
        return asesorFichaRepository.existsById(id);
    }
}
