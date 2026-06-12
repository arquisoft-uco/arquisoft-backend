package com.arquisoft.fichas.infrastructure.asesorficha.query.adapter.out.persistence;

import com.arquisoft.fichas.application.asesorficha.query.port.out.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaQueryOutputAdapter implements AsesorFichaQueryOutputPort {

    private final AsesorFichaJpaRepository asesorFichaJpaRepository;

    @Override
    public boolean existsById(UUID id) {
        return asesorFichaJpaRepository.existsById(id);
    }
}
