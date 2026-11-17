package com.arquisoft.evaluaciones.application.entregable.command.usecase.impl;

import com.arquisoft.evaluaciones.application.entregable.command.finder.EntregableExisteFinder;
import com.arquisoft.evaluaciones.application.entregable.command.result.AgregacionEntregableResult;
import com.arquisoft.evaluaciones.application.entregable.command.result.mapper.AgregacionEntregableResultMapper;
import com.arquisoft.evaluaciones.application.entregable.command.secondaryport.EntregableOutputPort;
import com.arquisoft.evaluaciones.application.entregable.command.secondaryport.mapper.EntregableMapper;
import com.arquisoft.evaluaciones.application.entregable.command.usecase.AgregarEntregableUseCase;
import com.arquisoft.evaluaciones.domain.entregable.EntregableDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.evaluaciones.EntregableKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarEntregableUseCaseImpl implements AgregarEntregableUseCase {

    private final EntregableOutputPort entregableOutputPort;
    private final EntregableExisteFinder entregableExisteFinder;
    private final AppLogger logger;

    @Override
    public AgregacionEntregableResult ejecutar(EntregableDomain entregable) {
        var existe = entregableExisteFinder.obtener(entregable.getId());
        logger.debug(EntregableKey.LOG_VERIFICACION_AGREGAR, entregable.getId(), existe);

        if (existe) {
            return AgregacionEntregableResultMapper.toResultDuplicado(entregable);
        }

        entregableOutputPort.guardar(EntregableMapper.toEntity(entregable));
        return AgregacionEntregableResultMapper.toResultAgregado(entregable);
    }
}
