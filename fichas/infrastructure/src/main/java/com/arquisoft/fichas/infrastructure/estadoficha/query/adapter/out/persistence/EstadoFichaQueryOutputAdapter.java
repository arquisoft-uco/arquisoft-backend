package com.arquisoft.fichas.infrastructure.estadoficha.query.adapter.out.persistence;

import com.arquisoft.fichas.application.estadoficha.query.port.out.EstadoFichaQueryOutputPort;
import com.arquisoft.fichas.application.estadoficha.query.readmodel.EstadoFichaReadModel;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaJpaRepository;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EstadoFichaQueryOutputAdapter implements EstadoFichaQueryOutputPort {

    private final EstadoFichaJpaRepository jpaRepository;
    private final EstadoFichaMapper mapper;

    @Override
    public Optional<EstadoFichaReadModel> buscarPorNombre(String nombre) {
        return jpaRepository.findByNombre(nombre)
                .map(mapper::toReadModel);
    }
}
