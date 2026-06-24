package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilCommandOutputAdapter implements EstadoFichaPerfilOutputPort {

    private final EstadoFichaPerfilJpaRepository jpaRepository;
    private final EstadoFichaPerfilMapper mapper;

    @Override
    public void guardar(EstadoFichaPerfilAggregate aggregate) {
        var entity = mapper.toJpaEntity(aggregate);
        jpaRepository.save(entity);
    }
}
