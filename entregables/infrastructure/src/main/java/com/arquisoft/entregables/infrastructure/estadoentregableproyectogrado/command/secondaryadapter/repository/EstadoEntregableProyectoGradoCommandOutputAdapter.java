package com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.EstadoEntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.estadoentregableproyectogrado.command.secondaryport.entity.EstadoEntregableProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.estadoentregableproyectogrado.command.secondaryadapter.mapper.EstadoEntregableProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EstadoEntregableProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstadoEntregableProyectoGradoCommandOutputAdapter implements EstadoEntregableProyectoGradoOutputPort {

    private final EstadoEntregableProyectoGradoCommandRepository estadoEntregableProyectoGradoCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrar(EstadoEntregableProyectoGradoEntity estado) {
        estadoEntregableProyectoGradoCommandRepository.save(EstadoEntregableProyectoGradoJpaMapper.toJpaEntity(estado));
        logger.debug(EstadoEntregableProyectoGradoKey.LOG_GUARDADO,
                estado.id(), estado.entregableProyectoGrado(), estado.estadoEntregable());
    }

    @Override
    public Optional<EstadoEntregableProyectoGradoEntity> obtenerEstadoActual(UUID entregableProyectoGrado) {
        return estadoEntregableProyectoGradoCommandRepository
                .findFirstByEntregableProyectoGradoOrderByFechaActualizacionDesc(entregableProyectoGrado)
                .map(EstadoEntregableProyectoGradoJpaMapper::toEntity);
    }
}
