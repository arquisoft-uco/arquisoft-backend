package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilCommandOutputAdapter implements EstadoFichaPerfilOutputPort {

    private final EstadoFichaPerfilRepository jpaRepository;
    private final EstadoFichaRepository estadoFichaRepository;

    @Override
    public void guardar(EstadoFichaPerfilAggregate aggregate) {
        var estadoFichaRef =
                estadoFichaRepository.getReferenceById(aggregate.getEstadoFicha().getId());
        var entity = EstadoFichaPerfilMapper.toEntity(aggregate, estadoFichaRef);
        jpaRepository.save(entity);
    }
}
