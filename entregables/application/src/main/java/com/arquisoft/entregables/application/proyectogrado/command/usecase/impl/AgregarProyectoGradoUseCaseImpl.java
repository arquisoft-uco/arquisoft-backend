package com.arquisoft.entregables.application.proyectogrado.command.usecase.impl;

import com.arquisoft.entregables.application.proyectogrado.command.finder.ProyectoGradoExisteFinder;
import com.arquisoft.entregables.application.proyectogrado.command.result.AgregacionProyectoGradoResult;
import com.arquisoft.entregables.application.proyectogrado.command.result.mapper.AgregacionProyectoGradoResultMapper;
import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.ProyectoGradoOutputPort;
import com.arquisoft.entregables.application.proyectogrado.command.secondaryport.mapper.ProyectoGradoMapper;
import com.arquisoft.entregables.application.proyectogrado.command.usecase.AgregarProyectoGradoUseCase;
import com.arquisoft.entregables.domain.proyectogrado.ProyectoGradoDomain;
import com.arquisoft.shared.logger.AppLogger;
import com.arquisoft.shared.message.key.entregables.ProyectoGradoKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AgregarProyectoGradoUseCaseImpl implements AgregarProyectoGradoUseCase {

    private final ProyectoGradoOutputPort proyectoGradoOutputPort;
    private final ProyectoGradoExisteFinder proyectoGradoExisteFinder;
    private final AppLogger logger;

    @Override
    public AgregacionProyectoGradoResult ejecutar(ProyectoGradoDomain proyecto) {
        var existe = proyectoGradoExisteFinder.obtener(proyecto.getId());
        logger.debug(ProyectoGradoKey.LOG_VERIFICACION_AGREGAR, proyecto.getId(), existe);

        if (existe) {
            return AgregacionProyectoGradoResultMapper.toResultDuplicado(proyecto);
        }

        proyectoGradoOutputPort.guardar(ProyectoGradoMapper.toEntity(proyecto));
        return AgregacionProyectoGradoResultMapper.toResultAgregado(proyecto);
    }
}
