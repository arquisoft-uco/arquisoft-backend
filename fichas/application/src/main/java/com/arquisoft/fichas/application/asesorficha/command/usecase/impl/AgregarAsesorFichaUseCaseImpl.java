package com.arquisoft.fichas.application.asesorficha.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaPorIdFinder;
import com.arquisoft.fichas.application.asesorficha.command.result.AgregacionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.result.mapper.AgregacionAsesorFichaResultMapper;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.mapper.AsesorFichaMapper;
import com.arquisoft.fichas.application.asesorficha.command.usecase.AgregarAsesorFichaUseCase;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.AsesorFichaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarAsesorFichaUseCaseImpl implements AgregarAsesorFichaUseCase {

    private final AsesorFichaOutputPort asesorFichaOutputPort;
    private final AsesorFichaPorIdFinder asesorFichaPorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionAsesorFichaResult ejecutar(AsesorFichaDomain asesorFicha) {
        var vigente = asesorFichaPorIdFinder.obtener(asesorFicha.getId());
        logger.debug(AsesorFichaKey.LOG_VERIFICACION_AGREGAR, asesorFicha.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            asesorFichaOutputPort.guardar(AsesorFichaMapper.toEntity(asesorFicha));
            return AgregacionAsesorFichaResultMapper.toResultAgregada(asesorFicha);
        }

        if (!asesorFicha.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return AgregacionAsesorFichaResultMapper.toResultDescartada(asesorFicha, vigente.getOcurridoEn());
        }

        if (!vigente.estaEliminado()) {
            return AgregacionAsesorFichaResultMapper.toResultDuplicada(asesorFicha);
        }

        vigente.reactivar(asesorFicha.getIdentificador(), asesorFicha.getNombre(), asesorFicha.getEmail(),
                asesorFicha.getOcurridoEn());
        asesorFichaOutputPort.reactivar(AsesorFichaMapper.toEntity(vigente));
        return AgregacionAsesorFichaResultMapper.toResultReactivada(vigente);
    }
}
