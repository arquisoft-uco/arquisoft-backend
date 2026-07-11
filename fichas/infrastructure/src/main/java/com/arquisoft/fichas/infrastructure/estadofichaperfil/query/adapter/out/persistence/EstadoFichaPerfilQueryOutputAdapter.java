package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.adapter.out.persistence;

import com.arquisoft.fichas.application.estadofichaperfil.query.port.out.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilQueryOutputAdapter implements EstadoFichaPerfilQueryOutputPort {

    private final EstadoFichaPerfilJpaRepository jpaRepository;

    @Override
    public Optional<EstadoFicha> obtenerEstadoActual(UUID fichaPerfilId) {
        return jpaRepository
                .findFirstByFichaPerfilIdOrderByFechaActualizacionDesc(fichaPerfilId)
                .map(entity -> EstadoFicha.valueOf(entity.getEstadoFicha().getId()));
    }
}
