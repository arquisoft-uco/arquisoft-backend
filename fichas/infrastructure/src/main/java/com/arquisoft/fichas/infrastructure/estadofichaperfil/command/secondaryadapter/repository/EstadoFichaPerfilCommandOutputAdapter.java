package com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.repository;

import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.EstadoFichaPerfilOutputPort;
import com.arquisoft.fichas.application.estadofichaperfil.command.secondaryport.entity.EstadoFichaPerfilEntity;
import com.arquisoft.fichas.infrastructure.estadofichaperfil.command.secondaryadapter.mapper.EstadoFichaPerfilJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstadoFichaPerfilKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoFichaPerfilCommandOutputAdapter implements EstadoFichaPerfilOutputPort {

    private final EstadoFichaPerfilCommandRepository repository;
    private final AppLogger logger;

    @Override
    public void registrarEstadoInicial(EstadoFichaPerfilEntity estado) {
        repository.save(EstadoFichaPerfilJpaMapper.toJpaEntity(estado));
        logger.debug(EstadoFichaPerfilKey.LOG_GUARDADO,
                estado.id(), estado.fichaPerfilId());
    }

    @Override
    public Optional<EstadoFichaPerfilEntity> obtenerEstadoActual(UUID fichaPerfilId) {
        return repository.findFirstByFichaPerfilIdOrderByFechaActualizacionDesc(fichaPerfilId)
                .map(EstadoFichaPerfilJpaMapper::toEntity);
    }
}
