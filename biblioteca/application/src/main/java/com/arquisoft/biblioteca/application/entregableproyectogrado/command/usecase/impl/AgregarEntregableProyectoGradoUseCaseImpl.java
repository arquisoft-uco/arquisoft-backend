package com.arquisoft.biblioteca.application.entregableproyectogrado.command.usecase.impl;

import com.arquisoft.biblioteca.application.entregableproyectogrado.command.finder.EntregableProyectoGradoExisteFinder;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.result.AgregacionEntregableProyectoGradoResult;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.result.mapper.AgregacionEntregableProyectoGradoResultMapper;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.EntregableProyectoGradoOutputPort;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.secondaryport.mapper.EntregableProyectoGradoMapper;
import com.arquisoft.biblioteca.application.entregableproyectogrado.command.usecase.AgregarEntregableProyectoGradoUseCase;
import com.arquisoft.biblioteca.domain.entregableproyectogrado.EntregableProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.biblioteca.EntregableProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarEntregableProyectoGradoUseCaseImpl implements AgregarEntregableProyectoGradoUseCase {

    private final EntregableProyectoGradoOutputPort entregableProyectoGradoOutputPort;
    private final EntregableProyectoGradoExisteFinder entregableProyectoGradoExisteFinder;
    private final AppLogger logger;

    @Override
    public AgregacionEntregableProyectoGradoResult ejecutar(EntregableProyectoGradoDomain entregable) {
        var existe = entregableProyectoGradoExisteFinder.obtener(entregable.getId());
        logger.debug(EntregableProyectoGradoKey.LOG_VERIFICACION_AGREGAR, entregable.getId(), existe);

        if (existe) {
            return AgregacionEntregableProyectoGradoResultMapper.toResultDescartado(entregable);
        }

        entregableProyectoGradoOutputPort.guardar(EntregableProyectoGradoMapper.toEntity(entregable));
        return AgregacionEntregableProyectoGradoResultMapper.toResultAgregado(entregable);
    }
}
