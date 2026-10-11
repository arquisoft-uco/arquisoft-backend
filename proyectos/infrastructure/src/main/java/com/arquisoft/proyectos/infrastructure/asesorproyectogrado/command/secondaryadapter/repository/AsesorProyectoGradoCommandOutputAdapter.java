package com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.AsesorProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.asesorproyectogrado.command.secondaryport.entity.AsesorProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.asesorproyectogrado.command.secondaryadapter.mapper.AsesorProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AsesorProyectoGradoCommandOutputAdapter implements AsesorProyectoGradoOutputPort {

    private final AsesorProyectoGradoCommandRepository asesorProyectoGradoRepository;
    private final AppLogger logger;

    @Override
    public void asignar(AsesorProyectoGradoEntity vinculo) {
        asesorProyectoGradoRepository.save(AsesorProyectoGradoJpaMapper.toJpaEntity(vinculo));
        logger.debug(AsesorProyectoGradoKey.LOG_GUARDADO, vinculo.id(), vinculo.proyectoGrado());
    }

    @Override
    public boolean existePorAsesorYProyectoGradoYEstado(UUID asesor, UUID proyectoGrado, String estadoAsesor) {
        return asesorProyectoGradoRepository
                .existsByAsesorIdAndProyectoGradoIdAndEstadoAsesorId(asesor, proyectoGrado, estadoAsesor);
    }

    @Override
    public long contarPorProyectoGradoYEstado(UUID proyectoGrado, String estadoAsesor) {
        return asesorProyectoGradoRepository.countByProyectoGradoIdAndEstadoAsesorId(proyectoGrado, estadoAsesor);
    }

    @Override
    public Optional<AsesorProyectoGradoEntity> obtenerPorIdYProyectoGrado(UUID id, UUID proyectoGrado) {
        return asesorProyectoGradoRepository.findByIdAndProyectoGradoId(id, proyectoGrado)
                .map(AsesorProyectoGradoJpaMapper::toEntity);
    }

    @Override
    public void actualizarEstado(UUID id, String estadoAsesor) {
        asesorProyectoGradoRepository.actualizarEstadoAsesor(id, estadoAsesor);
        logger.debug(AsesorProyectoGradoKey.LOG_ACTUALIZADO, id, estadoAsesor);
    }
}
