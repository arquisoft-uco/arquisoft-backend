package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilQueryOutputAdapter implements EstadoFichaPerfilQueryOutputPort {

    private final EstadoFichaPerfilRepository repository;

    @Override
    public Optional<EstadoFicha> obtenerEstadoActual(UUID fichaPerfilId) {
        return repository
                .findFirstByFichaPerfilIdOrderByFechaActualizacionDesc(fichaPerfilId)
                .map(entity -> EstadoFicha.valueOf(entity.getEstadoFicha().getId()));
    }
}
