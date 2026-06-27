package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaJpaEntity;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilMapper;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilCommandOutputAdapter implements EstadoFichaPerfilOutputPort {

    private final EstadoFichaPerfilJpaRepository jpaRepository;

    @PersistenceContext(unitName = "fichas")
    private EntityManager entityManager;

    @Override
    public void guardar(EstadoFichaPerfilAggregate aggregate) {
        var estadoFichaRef = entityManager.getReference(
                EstadoFichaJpaEntity.class,
                aggregate.getEstadoFicha().getId()
        );
        var entity = EstadoFichaPerfilMapper.toJpaEntity(aggregate, estadoFichaRef);
        jpaRepository.save(entity);
    }
}
