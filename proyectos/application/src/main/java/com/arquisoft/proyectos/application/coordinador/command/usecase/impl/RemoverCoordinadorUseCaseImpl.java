package com.arquisoft.proyectos.application.coordinador.command.usecase.impl;

import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.proyectos.application.coordinador.command.result.RemocionCoordinadorResult;
import com.arquisoft.proyectos.application.coordinador.command.result.mapper.RemocionCoordinadorResultMapper;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.mapper.CoordinadorMapper;
import com.arquisoft.proyectos.application.coordinador.command.usecase.RemoverCoordinadorUseCase;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.CoordinadorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverCoordinadorUseCaseImpl implements RemoverCoordinadorUseCase {

    private final CoordinadorOutputPort coordinadorOutputPort;
    private final CoordinadorPorIdFinder coordinadorPorIdFinder;
    private final AppLogger logger;

    @Override
    public RemocionCoordinadorResult ejecutar(CoordinadorDomain entrada) {
        var vigente = coordinadorPorIdFinder.obtener(entrada.getId());
        logger.debug(CoordinadorKey.LOG_VERIFICACION_REMOVER, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            entrada.remover(entrada.getOcurridoEn());
            coordinadorOutputPort.guardar(CoordinadorMapper.toEntity(entrada));
            return RemocionCoordinadorResultMapper.toResultLapida(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return RemocionCoordinadorResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.remover(entrada.getOcurridoEn());
        coordinadorOutputPort.eliminarLogica(vigente.getId(), vigente.getEliminadoEn());
        return RemocionCoordinadorResultMapper.toResultRemovida(vigente);
    }
}
