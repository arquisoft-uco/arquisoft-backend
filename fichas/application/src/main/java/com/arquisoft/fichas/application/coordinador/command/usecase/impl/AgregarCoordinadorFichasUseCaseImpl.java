package com.arquisoft.fichas.application.coordinador.command.usecase.impl;

import com.arquisoft.fichas.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.fichas.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.fichas.application.coordinador.command.result.mapper.AgregacionCoordinadorResultMapper;
import com.arquisoft.fichas.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.fichas.application.coordinador.command.secondaryport.mapper.CoordinadorMapper;
import com.arquisoft.fichas.application.coordinador.command.usecase.AgregarCoordinadorFichasUseCase;
import com.arquisoft.fichas.domain.coordinador.CoordinadorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.CoordinadorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarCoordinadorFichasUseCaseImpl implements AgregarCoordinadorFichasUseCase {

    private final CoordinadorOutputPort coordinadorOutputPort;
    private final CoordinadorPorIdFinder coordinadorPorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionCoordinadorResult ejecutar(CoordinadorDomain coordinador) {
        var vigente = coordinadorPorIdFinder.obtener(coordinador.getId());
        logger.debug(CoordinadorKey.LOG_VERIFICACION_AGREGAR, coordinador.getId(), vigente.isPresent());

        if (vigente.isPresent()) {
            if (!coordinador.getOcurridoEn().isAfter(vigente.get().ocurridoEn())) {
                return AgregacionCoordinadorResultMapper.toResultDescartada(
                        coordinador, vigente.get().ocurridoEn());
            }
            return AgregacionCoordinadorResultMapper.toResultDuplicada(coordinador);
        }

        coordinadorOutputPort.guardar(CoordinadorMapper.toEntity(coordinador));
        return AgregacionCoordinadorResultMapper.toResultAgregada(coordinador);
    }
}
