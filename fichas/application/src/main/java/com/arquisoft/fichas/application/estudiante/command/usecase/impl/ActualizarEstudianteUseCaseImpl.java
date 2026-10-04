package com.arquisoft.fichas.application.estudiante.command.usecase.impl;

import com.arquisoft.fichas.application.estudiante.command.finder.EstudiantePorIdFinder;
import com.arquisoft.fichas.application.estudiante.command.result.ActualizacionEstudianteResult;
import com.arquisoft.fichas.application.estudiante.command.result.mapper.ActualizacionEstudianteResultMapper;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.fichas.application.estudiante.command.secondaryport.mapper.EstudianteMapper;
import com.arquisoft.fichas.application.estudiante.command.usecase.ActualizarEstudianteUseCase;
import com.arquisoft.fichas.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.EstudianteKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActualizarEstudianteUseCaseImpl implements ActualizarEstudianteUseCase {

    private final EstudianteOutputPort estudianteOutputPort;
    private final EstudiantePorIdFinder estudiantePorIdFinder;
    private final AppLogger logger;

    @Override
    public ActualizacionEstudianteResult ejecutar(EstudianteDomain entrada) {
        var vigente = estudiantePorIdFinder.obtener(entrada.getId());
        logger.debug(EstudianteKey.LOG_VERIFICACION_ACTUALIZAR, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            return ActualizacionEstudianteResultMapper.toResultNoReplicado(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return ActualizacionEstudianteResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.actualizar(entrada.getIdentificador(), entrada.getNombre(), entrada.getEmail(),
                entrada.getOcurridoEn());
        estudianteOutputPort.actualizar(EstudianteMapper.toEntity(vigente));
        return ActualizacionEstudianteResultMapper.toResultActualizada(vigente);
    }
}
