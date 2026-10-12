package com.arquisoft.proyectos.application.coordinador.command.usecase.impl;

import com.arquisoft.proyectos.application.coordinador.command.finder.CoordinadorPorIdFinder;
import com.arquisoft.proyectos.application.coordinador.command.result.AgregacionCoordinadorResult;
import com.arquisoft.proyectos.application.coordinador.command.result.mapper.AgregacionCoordinadorResultMapper;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.CoordinadorOutputPort;
import com.arquisoft.proyectos.application.coordinador.command.secondaryport.mapper.CoordinadorMapper;
import com.arquisoft.proyectos.application.coordinador.command.usecase.AgregarCoordinadorUseCase;
import com.arquisoft.proyectos.domain.coordinador.CoordinadorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.CoordinadorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarCoordinadorUseCaseImpl implements AgregarCoordinadorUseCase {

    private final CoordinadorOutputPort coordinadorOutputPort;
    private final CoordinadorPorIdFinder coordinadorPorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionCoordinadorResult ejecutar(CoordinadorDomain coordinador) {
        var vigente = coordinadorPorIdFinder.obtener(coordinador.getId());
        logger.debug(CoordinadorKey.LOG_VERIFICACION_AGREGAR, coordinador.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            coordinadorOutputPort.guardar(CoordinadorMapper.toEntity(coordinador));
            return AgregacionCoordinadorResultMapper.toResultAgregada(coordinador);
        }

        if (!coordinador.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return AgregacionCoordinadorResultMapper.toResultDescartada(coordinador, vigente.getOcurridoEn());
        }

        if (!vigente.estaEliminado()) {
            return AgregacionCoordinadorResultMapper.toResultDuplicada(coordinador);
        }

        vigente.reactivar(coordinador.getIdentificador(), coordinador.getNombre(), coordinador.getEmail(),
                coordinador.getOcurridoEn());
        coordinadorOutputPort.reactivar(CoordinadorMapper.toEntity(vigente));
        return AgregacionCoordinadorResultMapper.toResultReactivada(vigente);
    }
}
