package com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.repository;

import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.proyectos.application.estudianteproyectogrado.command.secondaryport.entity.EstudianteProyectoGradoEntity;
import com.arquisoft.proyectos.infrastructure.estudianteproyectogrado.command.secondaryadapter.mapper.EstudianteProyectoGradoJpaMapper;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EstudianteProyectoGradoCommandOutputAdapter implements EstudianteProyectoGradoOutputPort {

    private final EstudianteProyectoGradoCommandRepository estudianteProyectoGradoRepository;
    private final AppLogger logger;

    @Override
    public void vincular(List<EstudianteProyectoGradoEntity> vinculos) {
        estudianteProyectoGradoRepository.saveAll(
                vinculos.stream().map(EstudianteProyectoGradoJpaMapper::toJpaEntity).toList());
        logger.debug(EstudianteProyectoGradoKey.LOG_GUARDADOS,
                vinculos.getFirst().proyectoGrado(), vinculos.size());
    }

    @Override
    public long contarPorProyectoGrado(UUID proyectoGrado) {
        return estudianteProyectoGradoRepository.countByProyectoGradoId(proyectoGrado);
    }
}
