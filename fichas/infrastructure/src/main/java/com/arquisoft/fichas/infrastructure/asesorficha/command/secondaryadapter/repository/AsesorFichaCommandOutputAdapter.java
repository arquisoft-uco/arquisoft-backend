package com.arquisoft.fichas.infrastructure.asesorficha.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.domain.asesorficha.model.ContactoAsesorFicha;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaCommandOutputAdapter implements AsesorFichaOutputPort {

    private final AsesorFichaRepository asesorFichaRepository;

    @Override
    public boolean existePorId(UUID id) {
        return asesorFichaRepository.existsById(id);
    }

    @Override
    public Optional<ContactoAsesorFicha> buscarContactoPorId(UUID id) {
        return asesorFichaRepository.findById(id)
                .map(entity -> new ContactoAsesorFicha(entity.getId(), entity.getNombre(), entity.getEmail()));
    }
}
