package com.arquisoft.fichas.infrastructure.estadofichaperfil.query.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.application.estadofichaperfil.query.secondaryport.EstadoFichaPerfilQueryOutputPort;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.mapper.EstadoFichaPerfilJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilQueryOutputAdapter implements EstadoFichaPerfilQueryOutputPort {

    private final EstadoFichaPerfilQueryRepository repository;

    @Override
    public Optional<EstadoFichaPerfilEntity> obtenerEstadoActual(UUID fichaPerfilId) {
        return repository.findFirstByFichaPerfilIdOrderByFechaActualizacionDesc(fichaPerfilId)
                .map(EstadoFichaPerfilJpaMapper::toEntity);
    }
}
