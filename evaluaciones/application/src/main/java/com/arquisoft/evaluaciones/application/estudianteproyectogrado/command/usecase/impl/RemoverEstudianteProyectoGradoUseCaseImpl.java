package com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.finder.VinculoEstudianteProyectoGradoFinder;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result.RemocionEstudianteProyectoGradoResult;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.result.mapper.RemocionEstudianteProyectoGradoResultMapper;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.EstudianteProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.secondaryport.mapper.EstudianteProyectoGradoMapper;
import com.arquisoft.evaluaciones.application.estudianteproyectogrado.command.usecase.RemoverEstudianteProyectoGradoUseCase;
import com.arquisoft.evaluaciones.domain.estudianteproyectogrado.EstudianteProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EstudianteProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverEstudianteProyectoGradoUseCaseImpl implements RemoverEstudianteProyectoGradoUseCase {

    private final EstudianteProyectoGradoOutputPort estudianteProyectoGradoOutputPort;
    private final VinculoEstudianteProyectoGradoFinder vinculoEstudianteProyectoGradoFinder;
    private final AppLogger logger;

    @Override
    public RemocionEstudianteProyectoGradoResult ejecutar(EstudianteProyectoGradoDomain entrada) {
        var existente = vinculoEstudianteProyectoGradoFinder.obtener(entrada);
        logger.debug(EstudianteProyectoGradoKey.LOG_VERIFICACION_REMOVER,
                entrada.getProyectoGrado(), !existente.esVacio());

        if (existente.esVacio()) {
            entrada.remover(entrada.getOcurridoEn());
            estudianteProyectoGradoOutputPort.guardar(EstudianteProyectoGradoMapper.toEntity(entrada));
            return RemocionEstudianteProyectoGradoResultMapper.toResultLapida(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(existente.getOcurridoEn())) {
            return RemocionEstudianteProyectoGradoResultMapper.toResultDescartada(entrada, existente.getOcurridoEn());
        }

        existente.remover(entrada.getOcurridoEn());
        estudianteProyectoGradoOutputPort.guardar(EstudianteProyectoGradoMapper.toEntity(existente));
        return RemocionEstudianteProyectoGradoResultMapper.toResultRemovida(existente);
    }
}
