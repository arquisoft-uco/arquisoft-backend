package com.arquisoft.proyectos.application.estudiante.command.usecase.impl;

import com.arquisoft.proyectos.application.estudiante.command.finder.EstudianteProyectosPorIdFinder;
import com.arquisoft.proyectos.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.proyectos.application.estudiante.command.result.mapper.AgregacionEstudianteResultMapper;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.mapper.EstudianteMapper;
import com.arquisoft.proyectos.application.estudiante.command.usecase.AgregarEstudianteProyectosUseCase;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectosKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarEstudianteProyectosUseCaseImpl implements AgregarEstudianteProyectosUseCase {

    private final EstudianteOutputPort estudianteOutputPort;
    private final EstudianteProyectosPorIdFinder estudianteProyectosPorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionEstudianteResult ejecutar(EstudianteDomain estudiante) {
        var vigente = estudianteProyectosPorIdFinder.obtener(estudiante.getId());
        logger.debug(EstudianteProyectosKey.LOG_VERIFICACION_AGREGAR, estudiante.getId(), vigente.isPresent());

        if (vigente.isPresent()) {
            if (!estudiante.getOcurridoEn().isAfter(vigente.get().ocurridoEn())) {
                return AgregacionEstudianteResultMapper.toResultDescartada(
                        estudiante, vigente.get().ocurridoEn());
            }
            return AgregacionEstudianteResultMapper.toResultDuplicada(estudiante);
        }

        estudianteOutputPort.guardar(EstudianteMapper.toEntity(estudiante));
        return AgregacionEstudianteResultMapper.toResultAgregada(estudiante);
    }
}
