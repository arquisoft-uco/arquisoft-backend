package com.arquisoft.proyectos.application.asesor.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.proyectos.application.asesor.command.result.ActualizacionAsesorResult;
import com.arquisoft.proyectos.application.asesor.command.result.mapper.ActualizacionAsesorResultMapper;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.mapper.AsesorMapper;
import com.arquisoft.proyectos.application.asesor.command.usecase.ActualizarAsesorUseCase;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ActualizarAsesorUseCaseImpl implements ActualizarAsesorUseCase {

    private final AsesorOutputPort asesorOutputPort;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final AppLogger logger;

    @Override
    public ActualizacionAsesorResult ejecutar(AsesorDomain entrada) {
        var vigente = asesorPorIdFinder.obtener(entrada.getId());
        logger.debug(AsesorKey.LOG_VERIFICACION_ACTUALIZAR, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            return ActualizacionAsesorResultMapper.toResultNoReplicado(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return ActualizacionAsesorResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.actualizar(entrada.getIdentificador(), entrada.getNombre(), entrada.getEmail(),
                entrada.getOcurridoEn());
        asesorOutputPort.actualizar(AsesorMapper.toEntity(vigente));
        return ActualizacionAsesorResultMapper.toResultActualizada(vigente);
    }
}
