package com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.entregables.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.entregableproyectogrado.command.secondaryadapter.mapper.EntregableProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.EntregableProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableProyectoGradoCommandOutputAdapter implements EntregableProyectoGradoOutputPort {

    private final EntregableProyectoGradoCommandRepository entregableProyectoGradoCommandRepository;
    private final AppLogger logger;

    @Override
    public void registrar(EntregableProyectoGradoEntity entregable) {
        entregableProyectoGradoCommandRepository.save(EntregableProyectoGradoJpaMapper.toJpaEntity(entregable));
        logger.debug(EntregableProyectoGradoKey.LOG_GUARDADO, entregable.id());
    }

    @Override
    public int obtenerUltimaVersion(UUID proyectoGrado) {
        return entregableProyectoGradoCommandRepository.obtenerUltimaVersion(proyectoGrado);
    }

    @Override
    public Optional<EntregableProyectoGradoEntity> obtenerPorId(UUID id) {
        return entregableProyectoGradoCommandRepository.buscarBloqueadaPorId(id)
                .map(EntregableProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public void actualizarEstado(UUID entregableProyectoGrado, String estadoEntregable) {
        entregableProyectoGradoCommandRepository.actualizarEstado(entregableProyectoGrado, estadoEntregable);
        logger.debug(EntregableProyectoGradoKey.LOG_ESTADO_ACTUALIZADO, entregableProyectoGrado);
    }
}
