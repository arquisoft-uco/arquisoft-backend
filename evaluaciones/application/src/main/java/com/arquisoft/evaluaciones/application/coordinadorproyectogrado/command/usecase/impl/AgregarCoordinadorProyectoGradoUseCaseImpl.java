package com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.finder.CoordinadorProyectoGradoExisteFinder;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.result.AgregacionCoordinadorProyectoGradoResult;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.result.mapper.AgregacionCoordinadorProyectoGradoResultMapper;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.CoordinadorProyectoGradoOutputPort;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.secondaryport.mapper.CoordinadorProyectoGradoMapper;
import com.arquisoft.evaluaciones.application.coordinadorproyectogrado.command.usecase.AgregarCoordinadorProyectoGradoUseCase;
import com.arquisoft.evaluaciones.domain.coordinadorproyectogrado.CoordinadorProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.CoordinadorProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarCoordinadorProyectoGradoUseCaseImpl implements AgregarCoordinadorProyectoGradoUseCase {

    private final CoordinadorProyectoGradoOutputPort coordinadorProyectoGradoOutputPort;
    private final CoordinadorProyectoGradoExisteFinder coordinadorProyectoGradoExisteFinder;
    private final AppLogger logger;

    @Override
    public AgregacionCoordinadorProyectoGradoResult ejecutar(CoordinadorProyectoGradoDomain coordinador) {
        var existe = coordinadorProyectoGradoExisteFinder.obtener(coordinador.getProyectoGrado());
        logger.debug(CoordinadorProyectoGradoKey.LOG_VERIFICACION_AGREGAR, coordinador.getProyectoGrado(), existe);

        if (existe) {
            return AgregacionCoordinadorProyectoGradoResultMapper.toResultDuplicada(coordinador);
        }

        coordinadorProyectoGradoOutputPort.guardar(CoordinadorProyectoGradoMapper.toEntity(coordinador));
        return AgregacionCoordinadorProyectoGradoResultMapper.toResultAgregada(coordinador);
    }
}
