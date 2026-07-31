package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaJpaRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilCommandOutputAdapter implements EstadoFichaPerfilOutputPort {

    private final EstadoFichaPerfilJpaRepository jpaRepository;
    private final EstadoFichaJpaRepository estadoFichaJpaRepository;

    @Override
    public void guardar(EstadoFichaPerfilAggregate aggregate) {
        var estadoFichaRef =
                estadoFichaJpaRepository.getReferenceById(aggregate.getEstadoFicha().getId());
        var entity = EstadoFichaPerfilMapper.toJpaEntity(aggregate, estadoFichaRef);
        jpaRepository.save(entity);
    }
}
