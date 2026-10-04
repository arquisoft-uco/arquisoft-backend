package com.arquisoft.proyectos.application.estudiante.command.usecase.impl;

import com.arquisoft.proyectos.application.estudiante.command.finder.EstudiantePorIdFinder;
import com.arquisoft.proyectos.application.estudiante.command.result.RemocionEstudianteResult;
import com.arquisoft.proyectos.application.estudiante.command.result.mapper.RemocionEstudianteResultMapper;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.EstudianteOutputPort;
import com.arquisoft.proyectos.application.estudiante.command.secondaryport.mapper.EstudianteMapper;
import com.arquisoft.proyectos.application.estudiante.command.usecase.RemoverEstudianteUseCase;
import com.arquisoft.proyectos.domain.estudiante.EstudianteDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.EstudianteProyectosKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteUseCaseImpl implements RemoverEstudianteUseCase {

    private final EstudianteOutputPort estudianteOutputPort;
    private final EstudiantePorIdFinder estudiantePorIdFinder;
    private final AppLogger logger;

    @Override
    public RemocionEstudianteResult ejecutar(EstudianteDomain entrada) {
        var vigente = estudiantePorIdFinder.obtener(entrada.getId());
        logger.debug(EstudianteProyectosKey.LOG_VERIFICACION_REMOVER, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            entrada.remover(entrada.getOcurridoEn());
            estudianteOutputPort.guardar(EstudianteMapper.toEntity(entrada));
            return RemocionEstudianteResultMapper.toResultLapida(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return RemocionEstudianteResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.remover(entrada.getOcurridoEn());
        estudianteOutputPort.eliminarLogica(vigente.getId(), vigente.getEliminadoEn());
        return RemocionEstudianteResultMapper.toResultRemovida(vigente);
    }
}
