package com.arquisoft.fichas.infrastructure.asesorficha.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.asesorficha.query.secondaryport.AsesorFichaQueryOutputPort;
import com.arquisoft.fichas.application.asesorficha.query.readmodel.AsesorContactoReadModel;
import com.arquisoft.fichas.infrastructure.asesorficha.persistence.AsesorFichaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorFichaQueryOutputAdapter implements AsesorFichaQueryOutputPort {

    private final AsesorFichaRepository asesorFichaRepository;

    @Override
    public boolean existePorId(UUID id) {
        return asesorFichaRepository.existsById(id);
    }

    @Override
    public Optional<AsesorContactoReadModel> buscarContactoPorId(UUID id) {
        return asesorFichaRepository.findById(id)
                .map(entity -> new AsesorContactoReadModel(
                        entity.getId(),
                        entity.getNombre(),
                        entity.getEmail()));
    }
}
