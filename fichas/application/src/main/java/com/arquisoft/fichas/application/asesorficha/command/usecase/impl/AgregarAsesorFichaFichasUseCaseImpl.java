package com.arquisoft.fichas.application.asesorficha.command.usecase.impl;

import com.arquisoft.fichas.application.asesorficha.command.finder.AsesorFichaFichasPorIdFinder;
import com.arquisoft.fichas.application.asesorficha.command.result.AgregacionAsesorFichaResult;
import com.arquisoft.fichas.application.asesorficha.command.result.mapper.AgregacionAsesorFichaResultMapper;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.AsesorFichaOutputPort;
import com.arquisoft.fichas.application.asesorficha.command.secondaryport.mapper.AsesorFichaMapper;
import com.arquisoft.fichas.application.asesorficha.command.usecase.AgregarAsesorFichaFichasUseCase;
import com.arquisoft.fichas.domain.asesorficha.AsesorFichaDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.fichas.AsesorFichaKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarAsesorFichaFichasUseCaseImpl implements AgregarAsesorFichaFichasUseCase {

    private final AsesorFichaOutputPort asesorFichaOutputPort;
    private final AsesorFichaFichasPorIdFinder asesorFichaFichasPorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionAsesorFichaResult ejecutar(AsesorFichaDomain asesorFicha) {
        var vigente = asesorFichaFichasPorIdFinder.obtener(asesorFicha.getId());
        logger.debug(AsesorFichaKey.LOG_VERIFICACION_AGREGAR, asesorFicha.getId(), vigente.isPresent());

        if (vigente.isPresent()) {
            if (!asesorFicha.getOcurridoEn().isAfter(vigente.get().ocurridoEn())) {
                return AgregacionAsesorFichaResultMapper.toResultDescartada(
                        asesorFicha, vigente.get().ocurridoEn());
            }
            return AgregacionAsesorFichaResultMapper.toResultDuplicada(asesorFicha);
        }

        asesorFichaOutputPort.guardar(AsesorFichaMapper.toEntity(asesorFicha));
        return AgregacionAsesorFichaResultMapper.toResultAgregada(asesorFicha);
    }
}
