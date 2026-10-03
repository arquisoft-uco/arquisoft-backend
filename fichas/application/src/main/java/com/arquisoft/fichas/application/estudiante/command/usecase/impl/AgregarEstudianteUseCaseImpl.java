package com.arquisoft.fichas.application.estudiante.command.usecase.impl;

import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantePorIdFinder;
import com.arquisoft.fichas.application.estudiante.command.result.AgregacionEstudianteResult;
import com.arquisoft.fichas.application.estudiante.command.result.mapper.AgregacionEstudianteResultMapper;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.mapper.EstudianteMapper;
import com.arquisoft.fichas.application.estudiante.command.usecase.AgregarEstudianteUseCase;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstudianteKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarEstudianteUseCaseImpl implements AgregarEstudianteUseCase {

    private final EstudianteOutputPort estudianteOutputPort;
    private final EstudiantePorIdFinder estudiantePorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionEstudianteResult ejecutar(EstudianteDomain estudiante) {
        var vigente = estudiantePorIdFinder.obtener(estudiante.getId());
        logger.debug(EstudianteKey.LOG_VERIFICACION_AGREGAR, estudiante.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            estudianteOutputPort.guardar(EstudianteMapper.toEntity(estudiante));
            return AgregacionEstudianteResultMapper.toResultAgregada(estudiante);
        }

        if (!estudiante.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return AgregacionEstudianteResultMapper.toResultDescartada(estudiante, vigente.getOcurridoEn());
        }

        if (!vigente.estaEliminado()) {
            return AgregacionEstudianteResultMapper.toResultDuplicada(estudiante);
        }

        vigente.reactivar(estudiante.getIdentificador(), estudiante.getNombre(), estudiante.getEmail(),
                estudiante.getOcurridoEn());
        estudianteOutputPort.reactivar(EstudianteMapper.toEntity(vigente));
        return AgregacionEstudianteResultMapper.toResultReactivada(vigente);
    }
}
