package com.arquisoft.fichas.application.asesorficha.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaPorIdFinder;
import com.arquisoft.fichas.application.asesorficha.command.result.RemocionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.result.mapper.RemocionAsesorFichaResultMapper;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.mapper.AsesorFichaMapper;
import com.arquisoft.fichas.application.asesorficha.command.usecase.RemoverAsesorFichaUseCase;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.AsesorFichaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverAsesorFichaUseCaseImpl implements RemoverAsesorFichaUseCase {

    private final AsesorFichaOutputPort asesorFichaOutputPort;
    private final AsesorFichaPorIdFinder asesorFichaPorIdFinder;
    private final AppLogger logger;

    @Override
    public RemocionAsesorFichaResult ejecutar(AsesorFichaDomain entrada) {
        var vigente = asesorFichaPorIdFinder.obtener(entrada.getId());
        logger.debug(AsesorFichaKey.LOG_VERIFICACION_REMOVER, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            entrada.remover(entrada.getOcurridoEn());
            asesorFichaOutputPort.guardar(AsesorFichaMapper.toEntity(entrada));
            return RemocionAsesorFichaResultMapper.toResultLapida(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return RemocionAsesorFichaResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.remover(entrada.getOcurridoEn());
        asesorFichaOutputPort.eliminarLogica(vigente.getId(), vigente.getEliminadoEn());
        return RemocionAsesorFichaResultMapper.toResultRemovida(vigente);
    }
}
