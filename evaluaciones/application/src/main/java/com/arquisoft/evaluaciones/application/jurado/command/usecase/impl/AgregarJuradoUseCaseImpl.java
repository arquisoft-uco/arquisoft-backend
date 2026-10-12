package com.arquisoft.evaluaciones.application.jurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.jurado.command.finder.JuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.jurado.command.result.AgregacionJuradoResult;
import com.arquisoft.evaluaciones.application.jurado.command.result.mapper.AgregacionJuradoResultMapper;
import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.JuradoOutputPort;
import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.mapper.JuradoMapper;
import com.arquisoft.evaluaciones.application.jurado.command.usecase.AgregarJuradoUseCase;
import com.arquisoft.evaluaciones.domain.jurado.JuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.JuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarJuradoUseCaseImpl implements AgregarJuradoUseCase {

    private final JuradoOutputPort juradoOutputPort;
    private final JuradoPorIdFinder juradoPorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionJuradoResult ejecutar(JuradoDomain jurado) {
        var vigente = juradoPorIdFinder.obtener(jurado.getId());
        logger.debug(JuradoKey.LOG_VERIFICACION_AGREGAR, jurado.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            juradoOutputPort.guardar(JuradoMapper.toEntity(jurado));
            return AgregacionJuradoResultMapper.toResultAgregada(jurado);
        }

        if (!jurado.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return AgregacionJuradoResultMapper.toResultDescartada(jurado, vigente.getOcurridoEn());
        }

        if (!vigente.estaEliminado()) {
            return AgregacionJuradoResultMapper.toResultDuplicada(jurado);
        }

        vigente.reactivar(jurado.getIdentificador(), jurado.getNombre(), jurado.getEmail(),
                jurado.getOcurridoEn());
        juradoOutputPort.reactivar(JuradoMapper.toEntity(vigente));
        return AgregacionJuradoResultMapper.toResultReactivada(vigente);
    }
}
