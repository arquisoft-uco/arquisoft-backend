package com.arquisoft.evaluaciones.application.jurado.command.usecase.impl;

import com.arquisoft.evaluaciones.application.jurado.command.finder.JuradoPorIdFinder;
import com.arquisoft.evaluaciones.application.jurado.command.result.RemocionJuradoResult;
import com.arquisoft.evaluaciones.application.jurado.command.result.mapper.RemocionJuradoResultMapper;
import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.JuradoOutputPort;
import com.arquisoft.evaluaciones.application.jurado.command.secondaryport.mapper.JuradoMapper;
import com.arquisoft.evaluaciones.application.jurado.command.usecase.RemoverJuradoUseCase;
import com.arquisoft.evaluaciones.domain.jurado.JuradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.JuradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverJuradoUseCaseImpl implements RemoverJuradoUseCase {

    private final JuradoOutputPort juradoOutputPort;
    private final JuradoPorIdFinder juradoPorIdFinder;
    private final AppLogger logger;

    @Override
    public RemocionJuradoResult ejecutar(JuradoDomain entrada) {
        var vigente = juradoPorIdFinder.obtener(entrada.getId());
        logger.debug(JuradoKey.LOG_VERIFICACION_REMOVER, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            entrada.remover(entrada.getOcurridoEn());
            juradoOutputPort.guardar(JuradoMapper.toEntity(entrada));
            return RemocionJuradoResultMapper.toResultLapida(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return RemocionJuradoResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.remover(entrada.getOcurridoEn());
        juradoOutputPort.eliminarLogica(vigente.getId(), vigente.getOcurridoEn());
        return RemocionJuradoResultMapper.toResultRemovida(vigente);
    }
}
