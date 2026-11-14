package com.arquisoft.entregables.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.entregables.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.entregables.infrastructure.asesorproyectogrado.command.secondaryadapter.mapper.AsesorProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.AsesorProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorProyectoGradoCommandOutputAdapter implements AsesorProyectoGradoOutputPort {

    private final AsesorProyectoGradoCommandRepository asesorProyectoGradoCommandRepository;
    private final AppLogger logger;

    @Override
    public Optional<AsesorProyectoGradoEntity> obtenerPorAsesorYProyectoGrado(UUID asesor, UUID proyectoGrado) {
        return asesorProyectoGradoCommandRepository.findByAsesorIdAndProyectoGradoId(asesor, proyectoGrado)
                .map(AsesorProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public Optional<UUID> obtenerAsesorVigentePorProyectoGrado(UUID proyectoGrado) {
        return asesorProyectoGradoCommandRepository.findAsesorVigenteByProyectoGradoId(proyectoGrado)
                .stream().findFirst();
    }

    @Override
    public Optional<UUID> obtenerAsesorVigentePorEntregable(UUID entregable) {
        return asesorProyectoGradoCommandRepository.findAsesorVigenteByEntregableId(entregable)
                .stream().findFirst();
    }

    @Override
    public void guardar(AsesorProyectoGradoEntity vinculo) {
        asesorProyectoGradoCommandRepository.save(AsesorProyectoGradoJpaMapper.toJpaEntity(vinculo));
        logger.debug(AsesorProyectoGradoKey.LOG_GUARDADO, vinculo.id());
    }
}
