package com.arquisoft.proyectos.application.asesor.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorProyectosPorIdFinder;
import com.arquisoft.proyectos.application.asesor.command.result.AgregacionAsesorResult;
import com.arquisoft.proyectos.application.asesor.command.result.mapper.AgregacionAsesorResultMapper;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.mapper.AsesorMapper;
import com.arquisoft.proyectos.application.asesor.command.usecase.AgregarAsesorProyectosUseCase;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarAsesorProyectosUseCaseImpl implements AgregarAsesorProyectosUseCase {

    private final AsesorOutputPort asesorOutputPort;
    private final AsesorProyectosPorIdFinder asesorProyectosPorIdFinder;
    private final AppLogger logger;

    @Override
    public AgregacionAsesorResult ejecutar(AsesorDomain asesor) {
        var vigente = asesorProyectosPorIdFinder.obtener(asesor.getId());
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
