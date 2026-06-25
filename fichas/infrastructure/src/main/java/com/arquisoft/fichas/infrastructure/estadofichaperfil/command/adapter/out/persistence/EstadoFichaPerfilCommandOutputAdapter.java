package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.adapter.out.persistence;

import com.arquisoft.fichas.domain.estadofichaperfil.aggregate.EstadoFichaPerfilAggregate;
import com.arquisoft.fichas.domain.estadofichaperfil.port.out.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaJpaRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilJpaRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilMapper;
import com.arquisoft.shared.message.FichasMessages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilCommandOutputAdapter implements EstadoFichaPerfilOutputPort {

    private final EstadoFichaPerfilJpaRepository jpaRepository;
    private final EstadoFichaJpaRepository estadoFichaJpaRepository;

    @Override
    public void guardar(EstadoFichaPerfilAggregate aggregate) {
        var nombre = aggregate.getEstadoFicha().getNombre();
        var estadoFichaEntity = estadoFichaJpaRepository
                .findByNombre(nombre)
                .orElseThrow(() -> new IllegalStateException(
                        FichasMessages.EstadoFicha.NOMBRE_NO_ENCONTRADO_MENSAJE.formatted(nombre)
                ));
        var entity = EstadoFichaPerfilMapper.toJpaEntity(aggregate, estadoFichaEntity.getId());
        jpaRepository.save(entity);
    }
}
