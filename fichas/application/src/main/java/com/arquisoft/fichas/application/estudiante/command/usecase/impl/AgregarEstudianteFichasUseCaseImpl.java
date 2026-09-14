package com.arquisoft.fichas.application.estudiante.command.usecase.impl;

import com.arquisoft.fichas.application.estudiante.command.finder.EstudianteFichasPorIdFinder;
import com.arquisoft.fichas.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.fichas.application.estudiante.command.result.mapper.AgregacionEstudianteResultMapper;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.mapper.EstudianteMapper;
import com.arquisoft.fichas.application.estudiante.command.usecase.AgregarEstudianteFichasUseCase;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstudianteKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarEstudianteFichasUseCaseImpl implements AgregarEstudianteFichasUseCase {

    private final EstudianteOutputPort estudianteOutputPort;
    private final EstudianteFichasPorIdFinder estudianteFichasPorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionEstudianteResult ejecutar(EstudianteDomain estudiante) {
        var vigente = estudianteFichasPorIdFinder.obtener(estudiante.getId());
        logger.debug(EstudianteKey.LOG_VERIFICACION_AGREGAR, estudiante.getId(), vigente.isPresent());

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
