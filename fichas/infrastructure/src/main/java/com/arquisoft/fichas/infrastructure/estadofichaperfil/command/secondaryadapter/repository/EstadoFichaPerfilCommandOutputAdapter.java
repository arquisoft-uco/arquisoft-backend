package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.domain.estadoficha.EstadoFicha;
import com.arquisoft.fichas.domain.estadofichaperfil.EstadoFichaPerfilDomain;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.infrastructure.estadoficha.persistence.EstadoFichaRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilRepository;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.persistence.EstadoFichaPerfilMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilCommandOutputAdapter implements EstadoFichaPerfilOutputPort {

    private final EstadoFichaPerfilRepository repository;
    private final EstadoFichaRepository estadoFichaRepository;

    @Override
    public void registrarEstadoInicial(EstadoFichaPerfilDomain aggregate) {
        var estadoFichaRef =
                estadoFichaRepository.getReferenceById(aggregate.getEstadoFicha().getId());
        var entity = EstadoFichaPerfilMapper.toEntity(aggregate, estadoFichaRef);
        repository.save(entity);
    }

    @Override
    public Optional<EstadoFicha> obtenerEstadoActual(UUID fichaPerfilId) {
        return repository
                .findFirstByFichaPerfilIdOrderByFechaActualizacionDesc(fichaPerfilId)
                .map(entity -> EstadoFicha.valueOf(entity.getEstadoFicha().getId()));
    }
}
