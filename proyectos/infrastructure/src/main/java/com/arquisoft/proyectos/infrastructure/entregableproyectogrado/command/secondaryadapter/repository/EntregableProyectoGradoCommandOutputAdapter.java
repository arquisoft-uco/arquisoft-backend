package com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.entregableproyectogrado.command.secondaryport.entity.EntregableProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.entregableproyectogrado.command.secondaryadapter.mapper.EntregableProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EntregableProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EntregableProyectoGradoCommandOutputAdapter implements EntregableProyectoGradoOutputPort {

    private final EntregableProyectoGradoCommandRepository entregableProyectoGradoRepository;
    private final AppLogger logger;

    @Override
    public void guardar(EntregableProyectoGradoEntity entregable) {
        entregableProyectoGradoRepository.save(EntregableProyectoGradoJpaMapper.toJpaEntity(entregable));
        logger.debug(EntregableProyectoGradoKey.LOG_GUARDADO, entregable.id(), entregable.proyectoGrado());
    }

    @Override
    public Optional<EntregableProyectoGradoEntity> obtenerPorId(UUID id) {
        return entregableProyectoGradoRepository.findById(id).map(EntregableProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<EntregableProyectoGradoEntity> obtenerUltimoPorProyectoGrado(UUID proyectoGrado) {
        return entregableProyectoGradoRepository
                .findFirstByProyectoGradoIdOrderByVersionEntregableDesc(proyectoGrado)
                .map(EntregableProyectoGradoJpaMapper::toEntity);
    }
}
