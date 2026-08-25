package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.mapper.EstadoFichaPerfilJpaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilCommandOutputAdapter implements EstadoFichaPerfilOutputPort {

    private final EstadoFichaPerfilCommandRepository repository;

    @Override
    public void registrarEstadoInicial(EstadoFichaPerfilEntity estado) {
        repository.save(EstadoFichaPerfilJpaMapper.toJpaEntity(estado));
    }

    @Override
    public Optional<EstadoFichaPerfilEntity> obtenerEstadoActual(UUID fichaPerfilId) {
        return repository.findFirstByFichaPerfilIdOrderByFechaActualizacionDesc(fichaPerfilId)
                .map(EstadoFichaPerfilJpaMapper::toEntity);
    }
}
