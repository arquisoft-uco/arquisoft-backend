package com.arquisoft.fichas.application.asesorficha.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaPorIdFinder;
import com.arquisoft.fichas.application.asesorficha.command.result.ActualizacionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.result.mapper.ActualizacionAsesorFichaResultMapper;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.mapper.AsesorFichaMapper;
import com.arquisoft.fichas.application.asesorficha.command.usecase.ActualizarAsesorFichaUseCase;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.AsesorFichaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActualizarAsesorFichaUseCaseImpl implements ActualizarAsesorFichaUseCase {

    private final AsesorFichaOutputPort asesorFichaOutputPort;
    private final AsesorFichaPorIdFinder asesorFichaPorIdFinder;
    private final AppLogger logger;

    @Override
    public ActualizacionAsesorFichaResult ejecutar(AsesorFichaDomain entrada) {
        var vigente = asesorFichaPorIdFinder.obtener(entrada.getId());
        logger.debug(AsesorFichaKey.LOG_VERIFICACION_ACTUALIZAR, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            return ActualizacionAsesorFichaResultMapper.toResultNoReplicado(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return ActualizacionAsesorFichaResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.actualizar(entrada.getIdentificador(), entrada.getNombre(), entrada.getEmail(),
                entrada.getOcurridoEn());
        asesorFichaOutputPort.actualizar(AsesorFichaMapper.toEntity(vigente));
        return ActualizacionAsesorFichaResultMapper.toResultActualizada(vigente);
    }
}
