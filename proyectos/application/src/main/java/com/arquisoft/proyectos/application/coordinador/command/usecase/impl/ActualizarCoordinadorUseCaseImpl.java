package com.arquisoft.proyectos.application.coordinador.command.usecase.impl;

import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.proyectos.application.coordinador.command.result.ActualizacionCoordinadorResult;
import com.arquisoft.proyectos.application.coordinador.command.result.mapper.ActualizacionCoordinadorResultMapper;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.mapper.CoordinadorMapper;
import com.arquisoft.proyectos.application.coordinador.command.usecase.ActualizarCoordinadorUseCase;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.CoordinadorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActualizarCoordinadorUseCaseImpl implements ActualizarCoordinadorUseCase {

    private final CoordinadorOutputPort coordinadorOutputPort;
    private final CoordinadorPorIdFinder coordinadorPorIdFinder;
    private final AppLogger logger;

    @Override
    public ActualizacionCoordinadorResult ejecutar(CoordinadorDomain entrada) {
        var vigente = coordinadorPorIdFinder.obtener(entrada.getId());
        logger.debug(CoordinadorKey.LOG_VERIFICACION_ACTUALIZAR, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            return ActualizacionCoordinadorResultMapper.toResultNoReplicado(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return ActualizacionCoordinadorResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.actualizar(entrada.getIdentificador(), entrada.getNombre(), entrada.getEmail(),
                entrada.getOcurridoEn());
        coordinadorOutputPort.actualizar(CoordinadorMapper.toEntity(vigente));
        return ActualizacionCoordinadorResultMapper.toResultActualizada(vigente);
    }
}
