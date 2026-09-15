package com.arquisoft.proyectos.application.asesor.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.proyectos.application.asesor.command.result.AgregacionAsesorResult;
import com.arquisoft.proyectos.application.asesor.command.result.mapper.AgregacionAsesorResultMapper;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.mapper.AsesorMapper;
import com.arquisoft.proyectos.application.asesor.command.usecase.AgregarAsesorUseCase;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarAsesorUseCaseImpl implements AgregarAsesorUseCase {

    private final AsesorOutputPort asesorOutputPort;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionAsesorResult ejecutar(AsesorDomain asesor) {
        var vigente = asesorPorIdFinder.obtener(asesor.getId());
        logger.debug(AsesorKey.LOG_VERIFICACION_AGREGAR, asesor.getId(), vigente.isPresent());

        if (vigente.isPresent()) {
            if (!asesor.getOcurridoEn().isAfter(vigente.get().ocurridoEn())) {
                return AgregacionAsesorResultMapper.toResultDescartada(
                        asesor, vigente.get().ocurridoEn());
            }
            return AgregacionAsesorResultMapper.toResultDuplicada(asesor);
        }

        asesorOutputPort.guardar(AsesorMapper.toEntity(asesor));
        return AgregacionAsesorResultMapper.toResultAgregada(asesor);
    }
}
