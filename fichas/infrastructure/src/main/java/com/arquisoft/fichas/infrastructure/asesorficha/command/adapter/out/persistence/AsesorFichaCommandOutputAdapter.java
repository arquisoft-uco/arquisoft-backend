package com.arquisoft.fichas.infrastructure.asesorficha.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.asesorficha.port.out.AsesorFichaOutputPort;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaCommandOutputAdapter implements AsesorFichaOutputPort {

    private final AsesorFichaJpaRepository asesorFichaJpaRepository;

    @Override
    public boolean existePorId(UUID id) {
        return asesorFichaJpaRepository.existsById(id);
    }
}
