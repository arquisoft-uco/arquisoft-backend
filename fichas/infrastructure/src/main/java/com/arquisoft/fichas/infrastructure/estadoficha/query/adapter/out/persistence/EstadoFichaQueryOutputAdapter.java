package com.arquisoft.fichas.infrastructure.estadoficha.query.adapter.out.persistence;

import com.arquisoft.fichas.application.estadoficha.query.port.out.EstadoFichaQueryOutputPort;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaEntity;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class EstadoFichaQueryOutputAdapter implements EstadoFichaQueryOutputPort {

    private final EstadoFichaRepository jpaRepository;

    @Override
    public List<EstadoFichaReadModel> findAll() {
        return jpaRepository.findAll()
                .stream()
                .map(this::toReadModel)
                .toList();
    }

    private EstadoFichaReadModel toReadModel(EstadoFichaEntity entity) {
        return new EstadoFichaReadModel(
                entity.getId(),
                entity.getNombre(),
                entity.getDescripcion()
        );
    }
}
