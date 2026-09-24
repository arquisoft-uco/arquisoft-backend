package com.arquisoft.proyectos.application.asesor.command.usecase.impl;

import com.arquisoft.proyectos.application.asesor.command.finder.AsesorPorIdFinder;
import com.arquisoft.proyectos.application.asesor.command.result.RemocionAsesorResult;
import com.arquisoft.proyectos.application.asesor.command.result.mapper.RemocionAsesorResultMapper;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.AsesorOutputPort;
import com.arquisoft.proyectos.application.asesor.command.secondaryport.mapper.AsesorMapper;
import com.arquisoft.proyectos.application.asesor.command.usecase.RemoverAsesorUseCase;
import com.arquisoft.proyectos.domain.asesor.AsesorDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.proyectos.AsesorKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RemoverAsesorUseCaseImpl implements RemoverAsesorUseCase {

    private final AsesorOutputPort asesorOutputPort;
    private final AsesorPorIdFinder asesorPorIdFinder;
    private final AppLogger logger;

    @Override
    public RemocionAsesorResult ejecutar(AsesorDomain entrada) {
        var vigente = asesorPorIdFinder.obtener(entrada.getId());
        logger.debug(AsesorKey.LOG_VERIFICACION_REMOVER, entrada.getId(), !vigente.esVacio());

        if (vigente.esVacio()) {
            entrada.remover(entrada.getOcurridoEn());
            asesorOutputPort.guardar(AsesorMapper.toEntity(entrada));
            return RemocionAsesorResultMapper.toResultLapida(entrada);
        }

        if (!entrada.getOcurridoEn().isAfter(vigente.getOcurridoEn())) {
            return RemocionAsesorResultMapper.toResultDescartada(entrada, vigente.getOcurridoEn());
        }

        vigente.remover(entrada.getOcurridoEn());
        asesorOutputPort.eliminarLogica(vigente.getId(), vigente.getEliminadoEn());
        return RemocionAsesorResultMapper.toResultRemovida(vigente);
    }
}
