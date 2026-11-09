package com.arquisoft.entregables.application.paqueteentregable.command.usecase.impl;

import com.arquisoft.entregables.application.paqueteentregable.command.finder.PaqueteEntregableExisteFinder;
import com.arquisoft.entregables.application.paqueteentregable.command.result.AgregacionPaqueteEntregableResult;
import com.arquisoft.entregables.application.paqueteentregable.command.result.mapper.AgregacionPaqueteEntregableResultMapper;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.PaqueteEntregableOutputPort;
import com.arquisoft.entregables.application.paqueteentregable.command.secondaryport.mapper.PaqueteEntregableMapper;
import com.arquisoft.entregables.application.paqueteentregable.command.usecase.AgregarPaqueteEntregableUseCase;
import com.arquisoft.entregables.domain.paqueteentregable.PaqueteEntregableDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.PaqueteEntregableKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarPaqueteEntregableUseCaseImpl implements AgregarPaqueteEntregableUseCase {

    private final PaqueteEntregableOutputPort paqueteEntregableOutputPort;
    private final PaqueteEntregableExisteFinder paqueteEntregableExisteFinder;
    private final AppLogger logger;

    @Override
    public AgregacionPaqueteEntregableResult ejecutar(PaqueteEntregableDomain paquete) {
        var existe = paqueteEntregableExisteFinder.obtener(paquete.getEntregableProyectoGrado());
        logger.debug(PaqueteEntregableKey.LOG_VERIFICACION_AGREGAR, paquete.getEntregableProyectoGrado(), existe);

        if (existe) {
            return AgregacionPaqueteEntregableResultMapper.toResultDuplicado(paquete);
        }

        paqueteEntregableOutputPort.guardar(PaqueteEntregableMapper.toEntity(paquete));
        return AgregacionPaqueteEntregableResultMapper.toResultAgregado(paquete);
    }
}
